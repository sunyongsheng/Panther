package top.aengus.panther.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import top.aengus.panther.core.GlobalConfig;

/**
 * <h3>picuang</h3>
 * <p>管理</p>
 *
 * @author : https://github.com/AdlerED
 * @date : 2019-11-07 17:05
 **/

@Controller
public class AdminController {
    @RequestMapping("/admin")
    @ResponseBody
    public ModelAndView admin() {
        ModelAndView modelAndView = new ModelAndView("admin");
        modelAndView.addObject("appConfLocation", ClassUtils.getDefaultClassLoader().getResource("").getPath() + "application.properties");
        modelAndView.addObject("version", GlobalConfig.getVersion());
        modelAndView.addObject("checkVersion", checkVersion());
        return modelAndView;
    }

    private String checkVersion() {
        String neededVersion = GlobalConfig.getVersion();
        String realVersion = GlobalConfig.get("version");
        if (neededVersion.equals(realVersion)) {
            return "";
        } else {
            return "<div class='alert alert-danger' style='text-shadow: none; margin: 0px 0px 0px'>请注意! 您的Picuang配置文件来自旧版 (" + realVersion + ")<br>" +
                    "请将旧配置文件<br>" + GlobalConfig.getConfigPath() + "<br>备份并删除, 重启Picuang服务端或点击\"生成新配置文件\"按钮使Picuang重新生成一个新版的配置文件; <br>" +
                    "再对照自动生成的新版本 (" + neededVersion + ") 配置文件, 将您备份的旧版配置文件中的数据替换 (除了version以外), 然后点击下方\"重载\"按钮. <br>" +
                    "如果您不想更新配置文件, 但想去除本通知, 请将 config.ini 中的 version 值修改为: \"" + neededVersion + "\" , 然后点击下方\"重载\"按钮. </div>" +
                    "<hr color='#6f5499' size='3' style='filter: alpha(opacity=100,finishopacity=0,style=3); margin-top: 36px; margin-bottom: 36px;' width='95%'/>";
        }
    }
}
