package com.zynboot.sys.handler.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zynboot.sys.response.org.OrgTreeRes;
import com.zynboot.sys.infrastructure.entity.SysOrganization;
import com.zynboot.sys.infrastructure.mapper.SysOrganizationMapper;
import com.zynboot.sys.util.CacheHelper;
import com.zynboot.sys.util.CacheKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织查询处理器。
 */
@Component
@RequiredArgsConstructor
public class OrgQueryHandler {

    private final SysOrganizationMapper organizationMapper;
    private final CacheHelper cacheHelper;

    public List<OrgTreeRes> getOrgTree() {
        return cacheHelper.getOrLoad(CacheKeys.ORG_TREE, CacheKeys.TREE_TTL, () -> {
            List<SysOrganization> all = organizationMapper.selectList(
                    new LambdaQueryWrapper<SysOrganization>()
                            .eq(SysOrganization::getStatus, 1)
                            .orderByAsc(SysOrganization::getSortOrder)
            );
            return buildTree(all, null);
        });
    }

    private List<OrgTreeRes> buildTree(List<SysOrganization> all, String parentId) {
        Map<String, List<SysOrganization>> parentMap = all.stream()
                .collect(Collectors.groupingBy(
                        org -> org.getParentId() == null ? "__root__" : org.getParentId(),
                        Collectors.toList()
                ));
        String key = parentId == null ? "__root__" : parentId;
        return parentMap.getOrDefault(key, List.of()).stream()
                .map(org -> OrgTreeRes.builder()
                        .id(org.getId())
                        .parentId(org.getParentId())
                        .code(org.getCode())
                        .name(org.getName())
                        .type(org.getType())
                        .sortOrder(org.getSortOrder())
                        .children(buildTree(all, org.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}
