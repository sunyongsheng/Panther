package top.aengus.panther.model.app;

import lombok.Data;
import top.aengus.panther.enums.AppRole;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateAppParam {

    @NotBlank(message = "App名称不能为空")
    private String name;

    @NotBlank(message = "英文名不能为空")
    private String englishName;

    @NotNull(message = "App必须设置初始角色！")
    private AppRole appRole;

}
