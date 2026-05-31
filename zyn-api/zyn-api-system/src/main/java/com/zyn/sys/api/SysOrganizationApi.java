package com.zyn.sys.api;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.org.OrgSaveCmd;
import com.zyn.sys.response.org.OrgRes;
import com.zyn.sys.response.org.OrgTreeRes;

import java.util.List;

/**
 * 组织管理 API。
 */
public interface SysOrganizationApi {

    /**
     * 查询组织列表。
     */
    ApiResponse<List<OrgRes>> list();

    /**
     * 获取组织树。
     */
    ApiResponse<List<OrgTreeRes>> tree();

    /**
     * 根据 ID 获取组织。
     */
    ApiResponse<OrgRes> getById(String id);

    /**
     * 创建组织。
     */
    ApiResponse<Void> create(OrgSaveCmd cmd);

    /**
     * 更新组织。
     */
    ApiResponse<Void> update(String id, OrgSaveCmd cmd);

    /**
     * 删除组织。
     */
    ApiResponse<Void> delete(String id);
}
