package top.aengus.panther.controller;

import cn.hutool.http.server.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.EncryptUtil;
import top.aengus.panther.tool.TokenUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
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
        if (request.getCookies() != null
                && Arrays.stream(request.getCookies()).anyMatch(cookie -> Constants.Cookie.ACCESS_TOKEN.equals(cookie.getName()) && TokenUtil.verify(cookie.getValue(), adminUsername))) {
            log.info("使用Cookie登录，用户名 {}", adminUsername);
            return "redirect:/admin/overview";
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
