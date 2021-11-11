package top.aengus.panther.model.setting;

import lombok.Data;
import top.aengus.panther.enums.NamingStrategy;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
@Data
public class UpdateAppSettingParam {

    @NotNull(message = "命名规则不可为空！")
    private NamingStrategy namingStrategy;

    @NotBlank(message = "默认保存目录不可为空！")
    private String defaultSaveDir;

}
