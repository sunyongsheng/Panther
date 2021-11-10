package top.aengus.panther.controller.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.aengus.panther.core.Response;
import top.aengus.panther.event.ShutdownEvent;
import top.aengus.panther.model.admin.ChangePasswordParam;
import top.aengus.panther.model.admin.AdminInfo;
import top.aengus.panther.model.config.PantherConfigDTO;
import top.aengus.panther.model.config.UpdateConfigParam;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.DateFormatter;
import top.aengus.panther.tool.EncryptUtil;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AdminController extends ApiV1Controller {

    private final PantherConfigService configService;
    private final ImageService imageService;
    private final AppInfoService appInfoService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AdminController(PantherConfigService configService, ApplicationEventPublisher eventPublisher, ImageService imageService, AppInfoService appInfoService) {
        this.configService = configService;
        this.eventPublisher = eventPublisher;
        this.imageService = imageService;
        this.appInfoService = appInfoService;
    }

    @GetMapping("/admin/overview/recent7day")
    public Response<List<Pair<String, Long>>> getRecent7DaysSummary() {
        Response<List<Pair<String, Long>>> response = new Response<>();
        LinkedList<Pair<String, Long>> result = new LinkedList<>();
        long endTime = System.currentTimeMillis();
        for (int i = 0; i < 7; i++) {
            long startTime = DateUtil.offsetDay(new Date(endTime), -1).getTime();
            result.add(0, Pair.of(DateFormatter.monthDayFormat(endTime), imageService.countInTimePeriodByUploadTime(startTime, endTime)));
            endTime = startTime;
        }
        return response.success().data(result);
    }

    @GetMapping("/admin/overview/top7upload")
    public Response<List<Pair<String, Long>>> getTop7UploadApp() {
        Response<List<Pair<String, Long>>> response = new Response<>();
        LinkedList<Pair<String, Long>> result = new LinkedList<>();
        appInfoService.findAll().forEach(appInfo -> result.add(Pair.of(appInfo.getName(), imageService.countByAppKey(appInfo.getAppKey()))));
        return response.success().data(result.stream().sorted((o1, o2) -> {
            long i = o1.getValue() - o2.getValue();
            if (i > 0) return -1;
            else if (i < 0) return 1;
            else return 0;
        }).collect(Collectors.toList()));
    }

    @PutMapping("/admin/password")
    public Response<Boolean> changePassword(@RequestBody @Validated ChangePasswordParam param) {
        Response<Boolean> response = new Response<>();
        String oldPassword = configService.getAdminPassword();
        if (!oldPassword.equals(EncryptUtil.encrypt(param.getOldPassword()))) {
            return response.badRequest().msg("旧密码不正确！").data(false);
        }
        configService.updateAdminPassword(param.getNewPassword());
        return response.success().msg("修改成功，请重新登录").data(true);
    }

    @GetMapping("/admin/info")
    public Response<AdminInfo> getAdminInfo() {
        AdminInfo info = new AdminInfo();
        info.setUsername(configService.getAdminUsername());
        info.setEmail(configService.getAdminEmail());
        return new Response<AdminInfo>().success().data(info);
    }

    @GetMapping("/admin/config")
    public Response<PantherConfigDTO> getPantherConfig() {
        PantherConfigDTO configDTO = new PantherConfigDTO();
        configDTO.setAdminUsername(configService.getAdminUsername());
        configDTO.setAdminEmail(configService.getAdminEmail());
        configDTO.setHostUrl(configService.getHostUrl());
        configDTO.setSaveRootPath(configService.getSaveRootPath());
        return new Response<PantherConfigDTO>().success().data(configDTO);
    }

    @PutMapping("/admin/config")
    public Response<Void> updatePantherConfig(@RequestParam(value = "changed", defaultValue = "hostUrl") String changed,
                                              @RequestBody @Validated UpdateConfigParam param) {
        Response<Void> response = new Response<>();
        configService.updateAdminEmail(param.getAdminEmail());
        if ("hostUrl".equals(changed)) {
            String newHostUrl = param.getUrlOrPath();
            imageService.updateImageHostUrl(newHostUrl);
            configService.updateHostUrl(newHostUrl);
            return response.success().msg("更新图床域名成功");
        } else if ("saveRootPath".equals(changed)) {
            String newRootPath = param.getUrlOrPath();
            imageService.updateImageSavePath(newRootPath);
            configService.updateSaveRootPath(param.getUrlOrPath());

            eventPublisher.publishEvent(new ShutdownEvent(this));
            return response.success().msg("更新图片存储目录成功，Panther已自动停止，请手动启动Panther，否则新上传的图片将无法被访问！");
        }
        return response.badRequest().msg("请求不合法！");
    }

}
