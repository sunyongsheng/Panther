package top.aengus.panther.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Response;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.CreateAppParam;
import top.aengus.panther.model.app.UpdateAppParam;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.model.setting.UpdateAppSettingParam;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.service.PantherConfigService;

import java.util.List;

@RestController
public class AppInfoController extends ApiV1Controller {

    private final AppInfoService appInfoService;
    private final ImageService imageService;
    private final PantherConfigService pantherConfigService;

    @Autowired
    public AppInfoController(AppInfoService appInfoService, ImageService imageService, PantherConfigService pantherConfigService) {
        this.appInfoService = appInfoService;
        this.imageService = imageService;
        this.pantherConfigService = pantherConfigService;
    }

    @PostMapping("/app")
    public Response<String> registerApp(@RequestBody @Validated CreateAppParam appParam) {
        Response<String> response = new Response<>();
        String appKey = appInfoService.createApp(appParam, pantherConfigService.getAdminUsername());
        return response.success().msg("注册成功，请妥善保管AppKey").data(appKey);
    }

    @PostMapping("/app/uploadToken")
    public Response<String> generateUploadToken(@RequestParam("app_key") String appKey) {
        return new Response<String>().success().msg("生成Token成功").data(appInfoService.generateUploadToken(appKey));
    }

    @PutMapping("/app")
    public Response<AppDTO> updateAppInfo(@RequestParam("app_key") String appKey,
                                          @RequestBody @Validated UpdateAppParam param) {
        return new Response<AppDTO>().success().msg("保存成功").data(appInfoService.updateAppInfo(appKey, param));
    }

    @PostMapping("/app/lock")
    public Response<Void> lockApp(@RequestParam("app_key") String appKey) {
        appInfoService.updateAppStatus(appKey, AppStatus.LOCKED);
        return new Response<Void>().success().msg("锁定App成功！");
    }

    @PostMapping("/app/unlock")
    public Response<Void> unlockApp(@RequestParam("app_key") String appKey) {
        appInfoService.updateAppStatus(appKey, AppStatus.NORMAL);
        return new Response<Void>().success().msg("解锁成功！");
    }

    @PostMapping("/app/delete")
    public Response<Void> deleteApp(@RequestParam("app_key") String appKey) {
        appInfoService.updateAppStatus(appKey, AppStatus.DELETED);
        imageService.deleteImagesByAppKey(appKey);
        return new Response<Void>().success().msg("删除成功！");
    }

    @PostMapping("/app/undelete")
    public Response<Void> undeleteApp(@RequestParam("app_key") String appKey) {
        appInfoService.updateAppStatus(appKey, AppStatus.NORMAL);
        imageService.undeleteImagesByAppKey(appKey);
        return new Response<Void>().success().msg("恢复成功！");
    }

    @PutMapping("/app/avatar")
    public Response<Void> updateAppIcon(@RequestParam("app_key") String appKey,
                                        @RequestParam("file") MultipartFile file) {
        Response<Void> response = new Response<>();
        ImageDTO result = imageService.saveImage(file, null, appKey, true);
        appInfoService.updateAppAvatar(appKey, result.getUrl());
        return response.success().msg("更新成功");
    }

    @PutMapping("/app/setting")
    public Response<Void> updateAppSetting(@RequestParam("app_key") String appKey,
                                           @RequestBody @Validated UpdateAppSettingParam param) {
        appInfoService.updateAppSetting(appKey, param);
        return new Response<Void>().success().msg("保存成功");
    }

    @GetMapping("/app/{appKey}")
    public Response<AppDTO> findApp(@PathVariable("appKey") String appKey) {
        Response<AppDTO> response = new Response<>();
        return response.success().data(appInfoService.findDTOByAppKey(appKey));
    }

    @GetMapping("/app}")
    public Response<List<AppDTO>> searchApp(@RequestParam("query") String query) {
        Response<List<AppDTO>> response = new Response<>();
        return response.success().data(appInfoService.findDTOByName(query));
    }

    @GetMapping("/apps")
    public Response<Page<AppDTO>> getAllApps(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        Response<Page<AppDTO>> response = new Response<>();
        // 因为ImageService和AppInfoService不能循环引用，所以totalImages字段在此填充
        return response.success().msg("获取成功").data(appInfoService.findDTOsByOwner(pantherConfigService.getAdminUsername(), page, pageSize).map(app -> {
            app.setTotalImages(imageService.countByAppKey(app.getAppKey()));
            return app;
        }));
    }

}
