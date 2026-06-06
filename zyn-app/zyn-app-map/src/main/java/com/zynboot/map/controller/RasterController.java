package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.domain.aggregate.SourceAggregate;
import com.zynboot.map.domain.repository.SourceRepository;
import com.zynboot.map.infrastructure.entity.MapSourceRaster;
import com.zynboot.map.infrastructure.mapper.MapSourceRasterMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.zynboot.infra.web.version.ApiVersion;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@ApiVersion("1")
@RequestMapping("/map/source")
public class RasterController {

    private final SourceRepository sourceRepository;
    private final MapSourceRasterMapper rasterMapper;
    private final com.zynboot.infra.storage.service.StorageService storageService;

    @GetMapping("/{id}/raster/meta")
    public ApiResponse<MapSourceRaster> getMeta(@PathVariable String id) {
        MapSourceRaster raster = rasterMapper.selectOne(
                new LambdaQueryWrapper<MapSourceRaster>().eq(MapSourceRaster::getSourceId, id));
        if (raster == null) throw BizException.notFound("栅格元数据");
        return ApiResponse.ok(raster);
    }

    @GetMapping("/{id}/raster/download")
    public void download(@PathVariable String id, HttpServletResponse response) {
        SourceAggregate source = sourceRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("数据源"));
        try {
            String storageKey = source.getEntity().getStorageKey();
            String fileName = source.getEntity().getName();
            String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName);
            try (InputStream in = storageService.openStream(storageKey);
                 OutputStream out = response.getOutputStream()) {
                in.transferTo(out);
            }
        } catch (Exception e) {
            log.error("Raster download failed: id={}", id, e);
            throw BizException.badRequest("下载失败");
        }
    }
}
