package top.aengus.panther.model.setting;

import lombok.Data;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
@Data
public class UpdateAppSettingParam {

    private Long appId;

    private String key;

    private String value;

}
