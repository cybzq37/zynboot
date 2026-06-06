package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.map.domain.repository.LayerRepository;
import com.zynboot.map.infrastructure.entity.MapFeature;
import com.zynboot.map.infrastructure.mapper.MapFeatureMapper;
import com.zynboot.map.infrastructure.mapper.MapSpatialMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 数据导出（流式输出，支持 GeoJSON / CSV）。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map")
public class ExportController {

    private final LayerRepository layerRepository;
    private final MapFeatureMapper featureMapper;
    private final MapSpatialMapper spatialMapper;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @GetMapping("/layer/{layerId}/export")
    public StreamingResponseBody export(
            @PathVariable String layerId,
            @RequestParam(defaultValue = "geojson") String format,
            @RequestParam(required = false) String sourceId,
            HttpServletResponse response) {

        layerRepository.findById(layerId)
                .orElseThrow(() -> BizException.notFound("图层"));

        String filename = "export_" + layerId + "." + format;
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

        switch (format.toLowerCase()) {
            case "csv" -> {
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
            }
            case "shapefile", "shp" -> {
                response.setContentType("application/zip");
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename + ".zip");
            }
            default -> {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
            }
        }

        return outputStream -> {
            try {
                switch (format.toLowerCase()) {
                    case "csv" -> exportCsv(layerId, sourceId, outputStream);
                    case "geojson" -> exportGeoJson(layerId, sourceId, outputStream);
                    default -> exportGeoJson(layerId, sourceId, outputStream);
                }
            } catch (Exception e) {
                log.error("Export failed: layerId={}, format={}", layerId, format, e);
                outputStream.write(("{\"error\":\"" + e.getMessage() + "\"}").getBytes());
            }
        };
    }

    private void exportGeoJson(String layerId, String sourceId, java.io.OutputStream outputStream) throws Exception {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)));
        writer.write("{\"type\":\"FeatureCollection\",\"features\":[");
        writer.flush();

        int page = 0;
        int pageSize = 500;
        boolean first = true;
        List<Map<String, Object>> features;

        do {
            features = spatialMapper.findAsGeoJson(layerId, pageSize, page * pageSize);
            for (Map<String, Object> feature : features) {
                if (!first) writer.write(",");
                writer.write(MAPPER.writeValueAsString(feature));
                first = false;
            }
            writer.flush();
            page++;
        } while (features.size() == pageSize);

        writer.write("]}");
        writer.flush();
    }

    private void exportCsv(String layerId, String sourceId, java.io.OutputStream outputStream) throws Exception {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)));

        // 写表头
        writer.println("id,source_id,lng,lat,properties");
        writer.flush();

        int page = 0;
        int pageSize = 500;
        List<Map<String, Object>> features;

        do {
            features = spatialMapper.findAsGeoJson(layerId, pageSize, page * pageSize);
            for (Map<String, Object> feature : features) {
                String id = String.valueOf(feature.get("id"));
                String sourceIdVal = String.valueOf(feature.get("source_id"));
                String properties = feature.get("properties") != null ?
                        MAPPER.writeValueAsString(feature.get("properties")).replace("\"", "\"\"") : "";
                String geomJson = feature.get("geometry") != null ? feature.get("geometry").toString() : "";
                String[] lngLat = extractLngLat(geomJson);
                writer.println(String.format("\"%s\",\"%s\",%s,%s,\"{\\\"properties\\\":%s}\"",
                        id, sourceIdVal, lngLat[0], lngLat[1], properties));
            }
            writer.flush();
            page++;
        } while (features.size() == pageSize);
    }

    private String[] extractLngLat(String geoJson) {
        // 简单提取 Point 坐标
        if (geoJson != null && geoJson.contains("\"coordinates\"")) {
            try {
                Map<String, Object> geom = MAPPER.readValue(geoJson, Map.class);
                Object coords = geom.get("coordinates");
                if (coords instanceof List<?> list && list.size() >= 2) {
                    return new String[]{String.valueOf(list.get(0)), String.valueOf(list.get(1))};
                }
            } catch (Exception ignored) {}
        }
        return new String[]{"0", "0"};
    }
}
