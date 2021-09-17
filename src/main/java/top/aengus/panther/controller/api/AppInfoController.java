package top.aengus.panther.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.ImageService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AppInfoController extends ApiV1Controller {

    private final AppInfoService appInfoService;
    private final ImageService imageService;

    @Autowired
    public AppInfoController(AppInfoService appInfoService, ImageService imageService) {
        this.appInfoService = appInfoService;
        this.imageService = imageService;
    }

    @GetMapping("/apps/{owner}")
    public Response<Page<AppDTO>> getAllApps(HttpServletRequest request,
                                             @PathVariable String owner,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        Response<Page<AppDTO>> response = new Response<>();
        return response.success().msg("获取成功").data(appInfoService.findDTOsByOwner(owner, page, pageSize));
    }

    @GetMapping("/appInfo/{appId}")
    public Response<AppDTO> findApp(HttpServletRequest request, @PathVariable("appId") String appId) {
        Response<AppDTO> response = new Response<>();
        return response.success().data(appInfoService.findDTOByAppId(appId));
    }

    @PutMapping("/appInfo/avatar")
    public Response<Void> updateAppIcon(HttpServletRequest request,
                                           @RequestParam("file") MultipartFile file) {
        Response<Void> response = new Response<>();
        AppInfo appInfo = (AppInfo) request.getAttribute(Constants.REQUEST_APP_INFO_INTERNAL);
        ImageDTO result = imageService.saveImage(file, null, appInfo);
        appInfoService.updateAppAvatar(appInfo.getAppId(), result.getUrl());
        return response.success().msg("更新成功");
    }
}
