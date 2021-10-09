package top.aengus.panther.model.app;

import lombok.Data;
import top.aengus.panther.enums.AppRole;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/10/10
 */
@Data
public class UpdateAppParam {

    @NotBlank(message = "App名称不能为空")
    private String name;

    @NotNull(message = "角色不允许为空")
    private AppRole role;

}
