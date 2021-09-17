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
import top.aengus.panther.service.FileService;
import top.aengus.panther.service.PantherConfigService;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/29
 */
@Component
public class CreateAppEventListener {

    private final AppSettingService appSettingService;
    private final PantherConfigService configService;
    private final FileService fileService;

    public CreateAppEventListener(AppSettingService appSettingService, PantherConfigService configService, FileService fileService) {
        this.appSettingService = appSettingService;
        this.configService = configService;
        this.fileService = fileService;
    }

    @Async
    @EventListener(CreateAppEvent.class)
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
        fileService.initAppWorkspace(configService.getSaveRootPath(), appName);
    }
}
