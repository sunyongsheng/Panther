package top.aengus.panther.model.app;

import lombok.Data;
import top.aengus.panther.enums.AppRole;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.enums.NamingStrategy;


/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/29
 */
@Data
public class AppDTO {

    private Long id;

    private String appKey;

    private AppRole role;

    private String name;

    private String englishName;

    private String avatarUrl;

    private Long createTime;

    private String phone;

    private String email;

    private AppStatus status;

    // Token
    private Boolean hasUploadToken1;

    private Long updateToken1GenTime;

    // Setting
    private NamingStrategy namingStrategy;

}
