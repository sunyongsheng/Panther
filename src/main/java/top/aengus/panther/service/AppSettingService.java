package top.aengus.panther.service;

import top.aengus.panther.model.setting.AppSetting;
import top.aengus.panther.model.setting.CreateAppSettingParam;
import top.aengus.panther.model.setting.UpdateAppSettingParam;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
public interface AppSettingService {

    AppSetting findAppSetting(Long appId, String key);

    void createAppSetting(CreateAppSettingParam param);

    void updateAppSetting(Long appId, UpdateAppSettingParam param);

    void deleteAppSetting(Long appId);

}
