package top.aengus.panther.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aengus.panther.dao.AppSettingRepository;
import top.aengus.panther.model.setting.AppSetting;
import top.aengus.panther.model.setting.CreateAppSettingParam;
import top.aengus.panther.model.setting.UpdateAppSettingParam;
import top.aengus.panther.service.AppSettingService;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
@Service
public class AppSettingServiceImpl implements AppSettingService {

    private final AppSettingRepository appSettingRepository;

    @Autowired
    public AppSettingServiceImpl(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    @Override
    public AppSetting findAppSetting(Long appId, String key) {
        return appSettingRepository.findByAppIdAndKey(appId, key);
    }

    @Override
    public void createAppSetting(CreateAppSettingParam param) {
        AppSetting appSetting = new AppSetting();
        BeanUtils.copyProperties(param, appSetting);
        appSetting.setUpdateTime(System.currentTimeMillis());
        appSettingRepository.save(appSetting);
    }

    @Override
    public void updateAppSetting(UpdateAppSettingParam param) {
        AppSetting appSetting = appSettingRepository.findByAppIdAndKey(param.getAppId(), param.getKey());
        if (appSetting == null) {
            appSetting = new AppSetting();
        }
        BeanUtils.copyProperties(param, appSetting);
        appSetting.setUpdateTime(System.currentTimeMillis());
        appSettingRepository.save(appSetting);
    }
}
