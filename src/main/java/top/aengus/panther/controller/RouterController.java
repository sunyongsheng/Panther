package top.aengus.panther.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import top.aengus.panther.core.Constants;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.DateFormatter;
import top.aengus.panther.tool.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Controller
public class RouterController {

    private final PantherConfigService configService;
    private final AppInfoService appInfoService;
    private final ImageService imageService;
    private final BuildProperties buildProperties;

    @Autowired
    public RouterController(PantherConfigService configService, AppInfoService appInfoService, ImageService imageService, BuildProperties buildProperties) {
        this.configService = configService;
        this.appInfoService = appInfoService;
        this.imageService = imageService;
        this.buildProperties = buildProperties;
    }


    @RequestMapping("/install")
    public String toInstallPage() {
        return "install";
    }

    @RequestMapping(value = {"/login", "/admin"})
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
        long installTime = configService.getInstallTime();
        model.addAttribute("runTime", DateFormatter.formatTimeDesc(System.currentTimeMillis() - installTime));
        model.addAttribute("installDesc", "安装于 " + DateFormatter.detailFormat(installTime));

        model.addAttribute("version", buildProperties.getVersion());
        return "admin/overview";
    }

    @RequestMapping("/admin/app-manager")
    public String toAppManagerPage(Model model) {
        model.addAttribute("version", buildProperties.getVersion());
        return "admin/app";
    }

    @RequestMapping("/admin/image-manager")
    public String toImageManagerPage(Model model) {
        model.addAttribute("version", buildProperties.getVersion());
        return "admin/image";
    }

    @RequestMapping("/admin/setting")
    public String toSettingPage(Model model) {
        model.addAttribute("version", buildProperties.getVersion());
        return "admin/setting";
    }

    @RequestMapping("/admin/changePassword")
    public String toChangePasswordPage(Model model) {
        model.addAttribute("version", buildProperties.getVersion());
        return "admin/password";
    }

}
