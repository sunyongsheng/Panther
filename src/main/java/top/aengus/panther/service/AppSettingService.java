package top.aengus.panther.service;

import lombok.NonNull;
import top.aengus.panther.enums.AppSettingKey;
import top.aengus.panther.model.app.AppInfo;
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
     * @param app App
     * @param key 设置项key
     * @return 符合条件的设置项，若没有则返回默认值
     */
    @NonNull
    String findAppSettingValue(AppInfo app, AppSettingKey key);

    /**
     * 创建App设置项
     */
    void createAppSetting(CreateAppSettingParam param);

    /**
     * 更新App设置项，不存在则创建
     */
    void updateAppSetting(AppInfo app, UpdateAppSettingParam param);

    /**
     * 删除App项
     */
    void deleteAppSetting(Long appId);

}
