package top.aengus.panther.component;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.aengus.panther.enums.AppSettingKey;
import top.aengus.panther.enums.NamingStrategy;
import top.aengus.panther.event.CreateAppEvent;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.setting.CreateAppSettingParam;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.AppSettingService;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.FileUtil;
import top.aengus.panther.tool.ImageDirUtil;

import java.io.File;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/29
 */
@Component
public class CreateAppEventListener {

    private final AppSettingService appSettingService;
    private final PantherConfigService configService;

    public CreateAppEventListener(AppSettingService appSettingService, PantherConfigService configService) {
        this.appSettingService = appSettingService;
        this.configService = configService;
    }

    @Async
    @EventListener
    public void handleCreateAppEvent(CreateAppEvent event) {
        if (event.getSource() instanceof AppInfoService) {
            AppInfo appInfo = event.getApp();
            initializeAppSetting(appInfo.getId());
            generateAppSpace(appInfo.getEnglishName());
        }
    }

    private void initializeAppSetting(Long appId) {
        for (AppSettingKey key : AppSettingKey.values()) {
            CreateAppSettingParam param = new CreateAppSettingParam();
            param.setAppId(appId);
            param.setKey(key.getCode());
            if (key == AppSettingKey.IMG_NAMING_STRATEGY) {
                param.setValue(NamingStrategy.UUID.name());
            }
            appSettingService.createAppSetting(param);
        }
    }

    private void generateAppSpace(String appName) {
        File appRoot = new File(configService.getSaveRootPath(), ImageDirUtil.NAME_APP);
        File appFile = new File(appRoot, appName);
        FileUtil.checkAndCreateDir(appFile);
    }
}
