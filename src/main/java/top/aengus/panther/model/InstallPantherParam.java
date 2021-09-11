package top.aengus.panther.model;

import lombok.Data;

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

    @NotBlank(message = "存储路径不可为空")
    private String saveRootPath;

    @NotEmpty(message = "不可为空")
    private List<String> imgDirs;

}
