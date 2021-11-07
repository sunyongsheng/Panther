package top.aengus.panther.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.ChangePasswordParam;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.EncryptUtil;

@RestController
public class AdminController extends ApiV1Controller {

    private final PantherConfigService configService;

    @Autowired
    public AdminController(PantherConfigService configService) {
        this.configService = configService;
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
}
