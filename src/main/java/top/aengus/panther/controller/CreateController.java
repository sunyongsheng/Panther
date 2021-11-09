package top.aengus.panther.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.aengus.panther.core.Response;
import top.aengus.panther.event.ShutdownEvent;
import top.aengus.panther.model.InstallPantherParam;
import top.aengus.panther.service.PantherConfigService;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/29
 */
@RestController
public class CreateController {

    private final PantherConfigService pantherConfigService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CreateController(PantherConfigService pantherConfigService, ApplicationEventPublisher eventPublisher) {
        this.pantherConfigService = pantherConfigService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/install")
    public Response<String> installPanther(@RequestBody @Validated InstallPantherParam param) {
        boolean result = pantherConfigService.install(param);
        if (result) {
            eventPublisher.publishEvent(new ShutdownEvent(this));
            return new Response<String>().success().msg("安装成功，Panther将自动停止，请手动启动Panther，否则仍无法正常使用").data("admin");
        }
        return new Response<String>().unknownError().msg("安装失败");
    }

}
