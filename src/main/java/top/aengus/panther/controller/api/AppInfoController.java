package top.aengus.panther.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.AppSettingService;
import top.aengus.panther.service.ImageService;

@RestController
public class AppInfoController extends ApiV1Controller {

    private final AppInfoService appInfoService;
    private final ImageService imageService;
    private final AppSettingService appSettingService;

    @Autowired
    public AppInfoController(AppInfoService appInfoService, ImageService imageService, AppSettingService appSettingService) {
        this.appInfoService = appInfoService;
        this.imageService = imageService;
        this.appSettingService = appSettingService;
    }

    @GetMapping("/apps/{owner}")
    public Response<Page<AppDTO>> getAllApps(@PathVariable String owner,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        Response<Page<AppDTO>> response = new Response<>();
        return response.success().msg("获取成功").data(appInfoService.findDTOsByOwner(owner, page, pageSize));
    }

    @GetMapping("/appInfo/{appKey}")
    public Response<AppDTO> findApp(@PathVariable("appKey") String appKey) {
        Response<AppDTO> response = new Response<>();
        return response.success().data(appInfoService.findDTOByAppKey(appKey));
    }

    @PutMapping("/appInfo/avatar")
    public Response<Void> updateAppIcon(@RequestParam("app_key") String appKey,
                                        @RequestParam("file") MultipartFile file) {
        Response<Void> response = new Response<>();
        AppInfo appInfo = appInfoService.findByAppKey(appKey);
        ImageDTO result = imageService.saveImage(file, null, appInfo);
        appInfoService.updateAppAvatar(appKey, result.getUrl());
        return response.success().msg("更新成功");
    }
}
