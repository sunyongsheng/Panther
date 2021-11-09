package top.aengus.panther.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.aengus.panther.controller.CreateController;
import top.aengus.panther.controller.api.AdminController;
import top.aengus.panther.event.ShutdownEvent;

@Slf4j
@Component
public class ApplicationEventListener {

    @Async
    @EventListener(ShutdownEvent.class)
    public void handleShutdownEvent(ShutdownEvent event) {
        if (event.getSource() instanceof CreateController) {
            log.info("安装Panther完成，即将重启");
            System.exit(9);
        } else if (event.getSource() instanceof AdminController) {
            log.info("修改图片存储路径，即将重启");
            System.exit(9);
        }
    }
}
