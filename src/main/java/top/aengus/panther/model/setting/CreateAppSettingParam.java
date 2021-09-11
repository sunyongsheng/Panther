package top.aengus.panther.model.setting;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
@Data
public class CreateAppSettingParam {

    @NotNull(message = "appId不能为空")
    private Long appId;

    @NotBlank(message = "key不可为空")
    private String key;

    @NotBlank(message = "value不可为空")
    private String value;
}
