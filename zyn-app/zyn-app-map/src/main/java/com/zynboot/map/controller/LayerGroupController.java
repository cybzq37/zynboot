package com.zynboot.map.controller;

import com.zynboot.kit.exception.BizException;
import com.zynboot.kit.response.ApiResponse;
import com.zynboot.map.command.group.GroupSaveCmd;
import com.zynboot.map.domain.aggregate.LayerGroupAggregate;
import com.zynboot.map.domain.repository.LayerGroupRepository;
import com.zynboot.map.handler.query.LayerGroupQueryHandler;
import com.zynboot.map.response.group.GroupTreeRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/group")
public class LayerGroupController {

    private final LayerGroupRepository groupRepository;
    private final LayerGroupQueryHandler groupQueryHandler;

    @GetMapping("/tree")
    public ApiResponse<List<GroupTreeRes>> tree() {
        return ApiResponse.ok(groupQueryHandler.getTree());
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody GroupSaveCmd cmd) {
        LayerGroupAggregate group = LayerGroupAggregate.create(cmd.getParentId(), cmd.getName());
        group.updateInfo(cmd.getName(), cmd.getDescription(), cmd.getSortOrder(), cmd.getIcon(), cmd.getColor());
        groupRepository.save(group);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody GroupSaveCmd cmd) {
        LayerGroupAggregate group = groupRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("分组"));
        group.updateInfo(cmd.getName(), cmd.getDescription(), cmd.getSortOrder(), cmd.getIcon(), cmd.getColor());
        groupRepository.update(group);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        groupRepository.delete(id);
        return ApiResponse.ok(null);
    }
}
