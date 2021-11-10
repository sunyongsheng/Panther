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

    /**
     * 查询{@link AppSetting#getAppId()} == [appId]且{@link AppSetting#getKey()} == [key]的设置项
     * @return 符合条件的设置项
     */
    AppSetting findAppSetting(Long appId, String key);

    /**
     * 创建App设置项
     */
    void createAppSetting(CreateAppSettingParam param);

    /**
     * 更新App设置项，不存在则创建
     */
    void updateAppSetting(Long appId, UpdateAppSettingParam param);

    /**
     * 删除App项
     */
    void deleteAppSetting(Long appId);

}
