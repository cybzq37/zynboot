package com.zynboot.map.controller;

import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapOperationLog;
import com.zynboot.map.infrastructure.mapper.MapOperationLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/audit")
public class AuditController {

    private final MapOperationLogMapper mapper;

    @GetMapping
    public ApiResponse<List<MapOperationLog>> list(
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String targetId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String operatorId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        LambdaQueryWrapper<MapOperationLog> wrapper = new LambdaQueryWrapper<MapOperationLog>()
                .eq(targetType != null, MapOperationLog::getTargetType, targetType)
                .eq(targetId != null, MapOperationLog::getTargetId, targetId)
                .eq(action != null, MapOperationLog::getAction, action)
                .eq(operatorId != null, MapOperationLog::getOperatorId, operatorId)
                .orderByDesc(MapOperationLog::getCreateTime)
                .last("LIMIT " + pageSize + " OFFSET " + (pageNum - 1) * pageSize);
        return ApiResponse.ok(mapper.selectList(wrapper));
    }

    @GetMapping("/{targetType}/{targetId}")
    public ApiResponse<List<MapOperationLog>> getByTarget(
            @PathVariable String targetType, @PathVariable String targetId) {
        return ApiResponse.ok(mapper.selectList(
                new LambdaQueryWrapper<MapOperationLog>()
                        .eq(MapOperationLog::getTargetType, targetType)
                        .eq(MapOperationLog::getTargetId, targetId)
                        .orderByDesc(MapOperationLog::getCreateTime)));
    }
}
