package com.zynboot.sys.controller;

import com.zynboot.infra.storage.service.StorageService;
import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.kit.util.Md5Utils;
import com.zynboot.sys.domain.aggregate.FileAggregate;
import com.zynboot.sys.domain.repository.FileRepository;
import com.zynboot.sys.handler.query.FileQueryHandler;
import com.zynboot.sys.query.file.FileQuery;
import com.zynboot.sys.response.PageRes;
import com.zynboot.sys.response.file.FileRes;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class SysFileController {

    private final StorageService storageService;
    private final FileRepository fileRepository;
    private final FileQueryHandler fileQueryHandler;

    @PostMapping("/upload")
    public ApiResponse<FileRes> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) String bizId,
            @RequestParam(required = false) String remark) {

        if (file.isEmpty()) {
            throw BizException.badRequest("文件不能为空");
        }

        try {
            String originalName = file.getOriginalFilename();
            String extension = extractExtension(originalName);
            String contentType = file.getContentType();
            long fileSize = file.getSize();

            // 计算 MD5
            String md5 = Md5Utils.md5(file.getInputStream());

            // MD5 去重：相同文件不重复存储
            FileAggregate existing = fileRepository.findByMd5(md5).orElse(null);
            if (existing != null) {
                log.info("File dedup hit: md5={}, existing storageKey={}", md5, existing.getStorageKey());
                // 复用已有存储，创建新记录关联到当前业务
                FileAggregate dup = FileAggregate.create(
                        originalName, existing.getStorageKey(), fileSize,
                        contentType, extension, md5, bizType, bizId, null);
                fileRepository.save(dup);
                return ApiResponse.ok(fileQueryHandler.findById(dup.getId()));
            }

            // 新文件上传到 StorageService
            var uploaded = storageService.upload(file);
            String storageKey = uploaded.getKey();

            FileAggregate agg = FileAggregate.create(
                    originalName, storageKey, fileSize,
                    contentType, extension, md5, bizType, bizId, null);
            fileRepository.save(agg);

            log.info("File uploaded: name={}, key={}, size={}", originalName, storageKey, fileSize);
            return ApiResponse.ok(fileQueryHandler.findById(agg.getId()));

        } catch (Exception e) {
            log.error("File upload failed", e);
            throw BizException.badRequest("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<PageRes<FileRes>> list(FileQuery query) {
        return ApiResponse.ok(fileQueryHandler.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<FileRes> getById(@PathVariable String id) {
        FileRes res = fileQueryHandler.findById(id);
        if (res == null) {
            throw BizException.notFound("文件");
        }
        return ApiResponse.ok(res);
    }

    @GetMapping("/{id}/download")
    public void download(@PathVariable String id, HttpServletResponse response) {
        FileAggregate file = fileRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("文件"));
        try {
            String storageKey = file.getStorageKey();
            String encodedName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8)
                    .replace("+", "%20");
            response.setContentType(file.getEntity().getContentType());
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName);
            response.setHeader("Content-Length", String.valueOf(file.getEntity().getFileSize()));

            try (InputStream in = storageService.openStream(storageKey);
                 OutputStream out = response.getOutputStream()) {
                in.transferTo(out);
            }
        } catch (Exception e) {
            log.error("File download failed: id={}", id, e);
            throw BizException.badRequest("文件下载失败");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        FileAggregate file = fileRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("文件"));
        try {
            // 检查是否有其他记录引用同一 storageKey（MD5 去重场景）
            FileAggregate other = fileRepository.findByMd5(file.getMd5()).orElse(null);
            boolean hasOtherRef = other != null && !other.getId().equals(file.getId());

            fileRepository.delete(id);

            if (!hasOtherRef) {
                storageService.delete(file.getStorageKey());
            }

            log.info("File deleted: id={}, key={}", id, file.getStorageKey());
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("File delete failed: id={}", id, e);
            throw BizException.badRequest("文件删除失败");
        }
        return ApiResponse.ok(null);
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return null;
        }
        int dot = filename.lastIndexOf('.');
        return dot > 0 && dot < filename.length() - 1
                ? filename.substring(dot + 1).toLowerCase()
                : null;
    }
}
