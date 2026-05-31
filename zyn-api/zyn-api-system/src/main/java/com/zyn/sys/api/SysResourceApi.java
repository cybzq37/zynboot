package com.zyn.sys.api;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.resource.ResourceSaveCmd;
import com.zyn.sys.response.resource.ResourceRes;

import java.util.List;

/**
 * API 资源管理 API。
 */
public interface SysResourceApi {

    /**
     * 查询资源列表。
     */
    ApiResponse<List<ResourceRes>> list();

    /**
     * 根据 ID 获取资源。
     */
    ApiResponse<ResourceRes> getById(String id);

    /**
     * 创建资源。
     */
    ApiResponse<Void> create(ResourceSaveCmd cmd);

    /**
     * 更新资源。
     */
    ApiResponse<Void> update(String id, ResourceSaveCmd cmd);

    /**
     * 删除资源。
     */
    ApiResponse<Void> delete(String id);
}
