package top.aengus.panther.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import top.aengus.panther.core.Constants;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Controller
public class RouterController {

    private final PantherConfigService configService;
    private final AppInfoService appInfoService;
    private final ImageService imageService;

    @Autowired
    public RouterController(PantherConfigService configService, AppInfoService appInfoService, ImageService imageService) {
        this.configService = configService;
        this.appInfoService = appInfoService;
        this.imageService = imageService;
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
    public String toAdminPage(Model model) {
        model.addAttribute("username", configService.getAdminUsername());
        model.addAttribute("appCount", appInfoService.countAll());
        model.addAttribute("imageCount", imageService.countAll());
        model.addAttribute("runTime", configService.getRunningDuration());
        return "admin/overview";
    }

    @RequestMapping("/admin/app-manager")
    public String toAppManagerPage() {
        return "admin/app";
    }

    @RequestMapping("/shutdown")
    public void shutdown() {
        System.exit(0);
    }

}
