package top.aengus.panther.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import top.aengus.panther.service.PantherConfigService;

@Controller
public class RouterController {

    private final PantherConfigService configService;

    @Autowired
    public RouterController(PantherConfigService configService) {
        this.configService = configService;
    }

    @RequestMapping("/admin")
    public String admin() {
        if (configService.hasInstalled()) {
            return "admin";
        } else {
            return "install";
        }
    }

}
