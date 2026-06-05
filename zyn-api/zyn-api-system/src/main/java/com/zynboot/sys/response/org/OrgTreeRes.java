package com.zynboot.sys.response.org;

import lombok.Builder;
import lombok.Value;

import lombok.extern.jackson.Jacksonized;
import java.util.List;

@Value
@Jacksonized
@Builder
public class OrgTreeRes {

    String id;
    String parentId;
    String code;
    String name;
    Integer type;
    Integer sortOrder;
    List<OrgTreeRes> children;
}
