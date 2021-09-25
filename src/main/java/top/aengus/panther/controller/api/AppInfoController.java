package top.aengus.panther.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.app.CreateAppParam;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.ImageService;

@RestController
public class AppInfoController extends ApiV1Controller {

    private final AppInfoService appInfoService;
    private final ImageService imageService;

    @Autowired
    public AppInfoController(AppInfoService appInfoService, ImageService imageService) {
        this.appInfoService = appInfoService;
        this.imageService = imageService;
    }

    @PostMapping("/app")
    public Response<String> registerApp(@RequestBody @Validated CreateAppParam appParam) {
        Response<String> response = new Response<>();
        String appKey = appInfoService.createApp(appParam);
        return response.success().msg("注册成功，请妥善保管AppKey").data(appKey);
    }

    @PostMapping("/app/uploadToken")
    public Response<String> generateUploadToken(@RequestParam("app_key") String appKey) {
        return new Response<String>().success().msg("生成Token成功").data(appInfoService.generateUploadToken(appKey));
    }

    @PutMapping("/app/avatar")
    public Response<Void> updateAppIcon(@RequestParam("app_key") String appKey,
                                        @RequestParam("file") MultipartFile file) {
        Response<Void> response = new Response<>();
        AppInfo appInfo = appInfoService.findByAppKey(appKey);
        ImageDTO result = imageService.saveImage(file, null, appInfo);
        appInfoService.updateAppAvatar(appKey, result.getUrl());
        return response.success().msg("更新成功");
    }

    @GetMapping("/app/{appKey}")
    public Response<AppDTO> findApp(@PathVariable("appKey") String appKey) {
        Response<AppDTO> response = new Response<>();
        return response.success().data(appInfoService.findDTOByAppKey(appKey));
    }

    @GetMapping("/apps/{owner}")
    public Response<Page<AppDTO>> getAllApps(@PathVariable String owner,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        Response<Page<AppDTO>> response = new Response<>();
        return response.success().msg("获取成功").data(appInfoService.findDTOsByOwner(owner, page, pageSize));
    }

}
