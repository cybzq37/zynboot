package com.zynboot.sys.command.org;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.zynboot.kit.jackson.plugins.xss.Xss;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgSaveCmd {

    String id;
    String parentId;

    @Xss
    @NotBlank(message = "组织编码不能为空")
    @Size(max = 50, message = "组织编码长度不能超过50")
    String code;

    @Xss
    @NotBlank(message = "组织名称不能为空")
    @Size(max = 100, message = "组织名称长度不能超过100")
    String name;

    Integer type;
    String leaderId;

    @Size(max = 20, message = "手机号长度不能超过20")
    String phone;

    @Xss
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    String email;

    Integer sortOrder;
    Integer status;

    @Xss
    @Size(max = 500, message = "备注长度不能超过500")
    String remark;
}
