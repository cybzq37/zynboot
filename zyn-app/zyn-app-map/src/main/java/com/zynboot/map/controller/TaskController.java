package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.infrastructure.entity.MapAsyncTask;
import com.zynboot.map.infrastructure.mapper.MapAsyncTaskMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/task")
public class TaskController {

    private final MapAsyncTaskMapper mapper;

    @GetMapping
    public ApiResponse<List<MapAsyncTask>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<MapAsyncTask> wrapper = new LambdaQueryWrapper<MapAsyncTask>()
                .eq(type != null, MapAsyncTask::getType, type)
                .eq(status != null, MapAsyncTask::getStatus, status)
                .orderByDesc(MapAsyncTask::getCreatedAt);
        return ApiResponse.ok(mapper.selectList(wrapper));
    }

    @GetMapping("/{id}")
    public ApiResponse<MapAsyncTask> getById(@PathVariable String id) {
        MapAsyncTask task = mapper.selectById(id);
        if (task == null) throw BizException.notFound("任务");
        return ApiResponse.ok(task);
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable String id) {
        MapAsyncTask task = mapper.selectById(id);
        if (task == null) throw BizException.notFound("任务");
        if (!"PENDING".equals(task.getStatus()) && !"RUNNING".equals(task.getStatus())) {
            throw BizException.badRequest("任务已结束，无法取消");
        }
        task.setStatus("CANCELLED");
        mapper.updateById(task);
        return ApiResponse.ok(null);
    }
}
