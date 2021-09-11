package top.aengus.panther.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.InstallPantherParam;
import top.aengus.panther.model.app.CreateAppParam;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.PantherConfigService;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/29
 */
@RestController
public class CreateController {

    private final AppInfoService appInfoService;
    private final PantherConfigService pantherConfigService;

    @Autowired
    public CreateController(AppInfoService appInfoService, PantherConfigService pantherConfigService) {
        this.appInfoService = appInfoService;
        this.pantherConfigService = pantherConfigService;
    }

    @PostMapping("/install")
    public Response<String> installPanther(@RequestBody @Validated InstallPantherParam param) {
        boolean result = pantherConfigService.install(param);
        if (result) {
            return new Response<String>().success().msg("安装成功").data("admin");
        }
        return new Response<String>().unknownError().msg("安装失败");
    }

    @PostMapping("/create/app")
    public Response<String> registerApp(@RequestBody @Validated CreateAppParam appParam) {
        Response<String> response = new Response<>();
        String appId = appInfoService.createApp(appParam);
        return response.msg("注册成功，请妥善保管AppID").data(appId);
    }
}
