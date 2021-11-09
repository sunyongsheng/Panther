package top.aengus.panther.model.admin;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordParam {

    @NotBlank(message = "原始密码不可为空！")
    private String oldPassword;

    @Length(min = 6, max = 20, message = "密码长度需在6-20之间")
    private String newPassword;
}
