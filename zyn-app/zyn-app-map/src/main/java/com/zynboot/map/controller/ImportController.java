package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.domain.aggregate.SourceAggregate;
import com.zynboot.map.service.ImportService;
import com.zynboot.map.service.VersionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map")
public class ImportController {

    private final ImportService importService;
    private final VersionService versionService;

    // ── 矢量导入 ─────────────────────────────────────────────

    @PostMapping("/import")
    public ApiResponse<SourceAggregate> importVector(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String layerId,
            @RequestParam String sourceSrid,
            @RequestParam(required = false) String sourceName) {
        try {
            if (layerId == null || layerId.isBlank()) {
                throw BizException.badRequest("layerId 不能为空");
            }
            // 导入前自动创建版本快照
            versionService.createSnapshot(layerId, "IMPORT", "导入前自动快照");
            SourceAggregate source = importService.importVector(file, layerId, sourceSrid, sourceName);
            return ApiResponse.ok(source);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Vector import failed", e);
            throw BizException.badRequest("导入失败: " + e.getMessage());
        }
    }

    // ── 栅格导入（双通道）─────────────────────────────────────

    @PostMapping("/import/raster")
    public ApiResponse<SourceAggregate> importRaster(
            @RequestParam("file") MultipartFile file,
            @RequestParam String layerId,
            @RequestParam String sourceSrid,
            @RequestParam(required = false) String sourceName) {
        try {
            SourceAggregate source = importService.importRaster(file, layerId, sourceSrid,
                    sourceName != null ? sourceName : file.getOriginalFilename());
            return ApiResponse.ok(source);
        } catch (Exception e) {
            log.error("Raster import failed", e);
            throw BizException.badRequest("导入失败: " + e.getMessage());
        }
    }

    @PostMapping("/import/raster/register")
    public ApiResponse<SourceAggregate> registerRaster(@Valid @RequestBody RasterRegisterCmd cmd) {
        try {
            SourceAggregate source = importService.registerRaster(
                    cmd.getFilePath(), cmd.getLayerId(), cmd.getSourceSrid(), cmd.getSourceName());
            return ApiResponse.ok(source);
        } catch (Exception e) {
            log.error("Raster register failed", e);
            throw BizException.badRequest("注册失败: " + e.getMessage());
        }
    }

    // ── PostGIS 直查注册 ─────────────────────────────────────

    @PostMapping("/import/postgis")
    public ApiResponse<SourceAggregate> registerPostgis(@Valid @RequestBody PostgisImportCmd cmd) {
        try {
            SourceAggregate source = importService.registerPostgis(
                    cmd.getLayerId(), cmd.getSourceName(), cmd.getDataSourceId(),
                    cmd.getExternalSchema(), cmd.getExternalTable(),
                    cmd.getExternalGeomCol(), cmd.getExternalIdCol(), cmd.getSourceSrid());
            return ApiResponse.ok(source);
        } catch (Exception e) {
            log.error("PostGIS register failed", e);
            throw BizException.badRequest("注册失败: " + e.getMessage());
        }
    }

    // ── DTO ─────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RasterRegisterCmd {
        @NotBlank String filePath;
        @NotBlank String layerId;
        @NotBlank String sourceSrid;
        String sourceName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostgisImportCmd {
        @NotBlank String layerId;
        String sourceName;
        @NotBlank String dataSourceId;
        String externalSchema;
        @NotBlank String externalTable;
        String externalGeomCol;
        String externalIdCol;
        @NotBlank String sourceSrid;
    }
}
