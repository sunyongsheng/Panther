package top.aengus.panther.controller;

import cn.hutool.http.server.HttpServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
public class RouterController {

    private final PantherConfigService configService;

    @Autowired
    public RouterController(PantherConfigService configService) {
        this.configService = configService;
    }


    @RequestMapping("/install")
    public String toInstallPage() {
        return "install";
    }

    @RequestMapping("/login")
    public String toLoginPage(HttpServletRequest request) {
        String adminUsername = configService.getAdminUsername();
        if (Arrays.stream(request.getCookies()).anyMatch(cookie -> adminUsername.equals(cookie.getName()) && TokenUtil.verify(cookie.getValue(), adminUsername))) {
            return "admin/overview";
        }
        return "login";
    }

    @RequestMapping("/admin/overview")
    public String toAdminPage() {
        return "admin/overview";
    }

    @RequestMapping("/shutdown")
    public void shutdown() {
        System.exit(0);
    }

}
