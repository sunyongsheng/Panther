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

    private final PantherConfigService pantherConfigService;

    @Autowired
    public CreateController(PantherConfigService pantherConfigService) {
        this.pantherConfigService = pantherConfigService;
    }

    @PostMapping("/install")
    public Response<String> installPanther(@RequestBody @Validated InstallPantherParam param) {
        boolean result = pantherConfigService.install(param);
        if (result) {
            return new Response<String>().success().msg("安装成功，请手动重启Panther，否则仍无法正常使用").data("admin");
        }
        return new Response<String>().unknownError().msg("安装失败");
    }

}
