package top.aengus.panther.service;

import top.aengus.panther.model.InstallPantherParam;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/4
 */
public interface PantherConfigService {

    String getHostUrl();

    String getSaveRootPath();

    String getAdminUsername();

    String getAdminPassword();

    String getAdminEmail();

    String getRunningDuration();

    void updateSaveRootPath(String saveRootPath);

    void updateHostUrl(String hostUrl);

    void updateAdminPassword(String superAdminPassword);

    void updateAdminEmail(String superAdminEmail);

    boolean hasInstalled();

    boolean install(InstallPantherParam param);
}
