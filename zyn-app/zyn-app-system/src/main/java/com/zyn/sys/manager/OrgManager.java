package com.zyn.sys.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyn.api.sys.response.org.OrgTreeRes;
import com.zyn.sys.entity.SysOrganization;
import com.zyn.sys.service.SysOrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrgManager {

    private final SysOrganizationService organizationService;

    public List<OrgTreeRes> getOrgTree() {
        List<SysOrganization> all = organizationService.list(
                new LambdaQueryWrapper<SysOrganization>()
                        .eq(SysOrganization::getStatus, 1)
                        .orderByAsc(SysOrganization::getSort)
        );
        return buildTree(all, "0");
    }

    private List<OrgTreeRes> buildTree(List<SysOrganization> all, String parentId) {
        Map<String, List<SysOrganization>> parentMap = all.stream()
                .collect(Collectors.groupingBy(
                        org -> org.getParentId() == null ? "0" : org.getParentId(),
                        Collectors.toList()
                ));
        return parentMap.getOrDefault(parentId, List.of()).stream()
                .map(org -> OrgTreeRes.builder()
                        .id(org.getId())
                        .parentId(org.getParentId())
                        .orgCode(org.getOrgCode())
                        .orgName(org.getOrgName())
                        .orgType(org.getOrgType())
                        .sort(org.getSort())
                        .children(buildTree(all, org.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}
