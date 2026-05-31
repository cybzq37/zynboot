package com.zyn.sys.command.org;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgSaveCmd {

    String id;
    String parentId;
    String orgCode;
    String orgName;
    Integer orgType;
    String leaderId;
    String phone;
    String email;
    Integer sort;
    Integer status;
    String remark;
}
