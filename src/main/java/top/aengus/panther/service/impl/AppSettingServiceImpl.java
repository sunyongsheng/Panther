package top.aengus.panther.service.impl;

import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aengus.panther.dao.AppSettingRepository;
import top.aengus.panther.enums.AppSettingKey;
import top.aengus.panther.enums.NamingStrategy;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.InternalException;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.setting.AppSetting;
import top.aengus.panther.model.setting.CreateAppSettingParam;
import top.aengus.panther.model.setting.UpdateAppSettingParam;
import top.aengus.panther.service.AppSettingService;
import top.aengus.panther.service.FileService;
import top.aengus.panther.tool.FileUtil;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
@Service
public class AppSettingServiceImpl implements AppSettingService {

    private final AppSettingRepository appSettingRepository;

    private final FileService fileService;

    @Autowired
    public AppSettingServiceImpl(AppSettingRepository appSettingRepository, FileService fileService) {
        this.appSettingRepository = appSettingRepository;
        this.fileService = fileService;
    }

    @Override
    public @NonNull String findAppSettingValue(AppInfo app, AppSettingKey key) {
        AppSetting setting = appSettingRepository.findByAppIdAndKey(app.getId(), key.getCode());
        if (setting == null) {
            return getDefaultSettingValue(key, app.getEnglishName());
        }
        return setting.getValue();
    }

    private String getDefaultSettingValue(AppSettingKey key, String extraParam) {
        switch (key) {
            case IMG_NAMING_STRATEGY:
                return NamingStrategy.UUID.name();
            case DEFAULT_SAVE_DIR:
                return fileService.getAppWorkspaceDir(extraParam);
            default:
                throw new InternalException("不存在此设置项！");
        }
    }

    @Override
    public void createAppSetting(CreateAppSettingParam param) {
        AppSetting appSetting = new AppSetting();
        BeanUtils.copyProperties(param, appSetting);
        appSetting.setUpdateTime(System.currentTimeMillis());
        appSettingRepository.save(appSetting);
    }

    @Override
    public void updateAppSetting(AppInfo app, UpdateAppSettingParam param) {
        Long appId = app.getId();
        for (AppSettingKey settingKey : AppSettingKey.values()) {
            AppSetting appSetting = appSettingRepository.findByAppIdAndKey(appId, settingKey.getCode());
            if (appSetting == null) {
                appSetting = new AppSetting();
                appSetting.setAppId(appId);
                appSetting.setKey(settingKey.getCode());
            }
            if (settingKey == AppSettingKey.IMG_NAMING_STRATEGY) {
                appSetting.setValue(param.getNamingStrategy().name());
            } else if (settingKey == AppSettingKey.DEFAULT_SAVE_DIR) {
                if (FileUtil.isPathIllegal(param.getDefaultSaveDir())) {
                    throw new BadRequestException("请以 / 作为路径分隔符！");
                }
                String correctDir = FileUtil.ensureNoSuffix(FileUtil.ensurePrefix(param.getDefaultSaveDir()));
                if (FileUtil.isAppDirIllegal(FileUtil.ensureSuffix(correctDir), app.getEnglishName())) {
                    throw new BadRequestException("不允许上传到app目录下其他文件夹中");
                }
                appSetting.setValue(correctDir);
            } else {
                continue;
            }
            appSetting.setUpdateTime(System.currentTimeMillis());
            appSettingRepository.save(appSetting);
        }
    }

    @Override
    public void deleteAppSetting(Long appId) {
        appSettingRepository.deleteAll(appSettingRepository.findAllByAppId(appId));
    }
}
