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

    void updateSaveRootPath(String saveRootPath);

    void updateHostUrl(String hostUrl);

    boolean hasInstalled();

    boolean install(InstallPantherParam param);
}
