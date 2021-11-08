package top.aengus.panther.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/12
 */
@Data
public class InstallPantherParam {

    @NotBlank(message = "url不可空")
    private String hostUrl;

    private String saveRootPath;

    @Length(min = 6, max = 20, message = "用户名需在6-20位之间")
    private String adminUsername;

    @Length(min = 6, max = 20, message = "密码需要在6-20位之间")
    private String adminPassword;

    @Email(message = "邮箱地址不合法")
    private String adminEmail;

    @NotEmpty(message = "不可为空")
    private List<String> imgDirs;

}
