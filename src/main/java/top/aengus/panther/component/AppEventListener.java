package top.aengus.panther.component;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.aengus.panther.enums.AppSettingKey;
import top.aengus.panther.enums.NamingStrategy;
import top.aengus.panther.event.CreateAppEvent;
import top.aengus.panther.event.DeleteAppEvent;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.setting.CreateAppSettingParam;
import top.aengus.panther.service.*;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/29
 */
@Component
public class AppEventListener {

    private final AppSettingService appSettingService;
    private final PantherConfigService configService;
    private final FileService fileService;
    private final ImageService imageService;
    private final AppTokenService appTokenService;

    public AppEventListener(AppSettingService appSettingService, PantherConfigService configService, FileService fileService, ImageService imageService, AppTokenService appTokenService) {
        this.appSettingService = appSettingService;
        this.configService = configService;
        this.fileService = fileService;
        this.imageService = imageService;
        this.appTokenService = appTokenService;
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
            } else {
                continue;
            }
            appSettingService.createAppSetting(param);
        }
    }

    private void generateAppSpace(String appName) {
        fileService.initAppWorkspace(configService.getSaveRootPath(), appName);
    }

    @Async
    @EventListener(DeleteAppEvent.class)
    public void handleDeleteAppEvent(DeleteAppEvent event) {
        if (event.isForever()) {
            AppInfo app = event.getApp();
            String appKey = app.getAppKey();
            imageService.deleteImagesForeverByAppKey(appKey);
            appTokenService.deleteToken(appKey);
            appSettingService.deleteAppSetting(app.getId());
        } else {
            // 暂时不会走这里
            imageService.deleteImagesByAppKey(event.getApp().getAppKey());
        }
    }

}
