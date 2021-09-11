package top.aengus.panther.event;

import org.springframework.context.ApplicationEvent;
import top.aengus.panther.model.app.AppInfo;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
public class CreateAppEvent extends ApplicationEvent {

    private final AppInfo app;

    public CreateAppEvent(Object source, AppInfo app) {
        super(source);
        this.app = app;
    }

    public AppInfo getApp() {
        return app;
    }
}
