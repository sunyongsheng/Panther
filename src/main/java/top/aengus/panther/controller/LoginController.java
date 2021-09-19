package top.aengus.panther.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.aengus.panther.core.Response;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.EncryptUtil;
import top.aengus.panther.tool.TokenUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class LoginController {

    private final PantherConfigService configService;

    @Autowired
    public LoginController(PantherConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/adminLogin")
    public Response<String> login(HttpServletResponse response, @RequestParam String username, @RequestParam String password) {
        Response<String> res = new Response<>();
        String adminUsername = configService.getAdminUsername();
        String adminPassword = configService.getAdminPassword();
        if (adminUsername.equals(username) && adminPassword.equals(EncryptUtil.encrypt(password))) {
            log.info("管理员登录，用户名 {}", username);
            String token = TokenUtil.sign(adminUsername, 7);
            Cookie cookie = new Cookie(adminUsername, token);
            cookie.setMaxAge(7 * 24 * 3600);
            response.addCookie(cookie);
            return res.success().msg("登录成功").data(token);
        }
        log.warn("管理员登录失败，用户名 {}, 密码 {}", adminUsername, adminPassword);
        return res.noAuth().msg("用户名或密码错误");
    }

}
