package top.aengus.panther.model.app;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CreateAppParam {

    @NotBlank(message = "App名称不能为空")
    private String name;

    @NotBlank(message = "英文名不能为空")
    private String englishName;

    @Length(message = "手机号必须为11位")
    private String phone;

    @Email(message = "邮箱地址不合法")
    private String email;

    @Length(min = 6, max = 20, message = "密码长度需在6到20位之间")
    private String password;

}
