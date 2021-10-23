package top.aengus.panther.event;

import org.springframework.context.ApplicationEvent;
import top.aengus.panther.model.app.AppInfo;

public class UndeleteAppEvent extends ApplicationEvent {

    private final AppInfo app;

    public UndeleteAppEvent(Object source, AppInfo app) {
        super(source);
        this.app = app;
    }

    public AppInfo getApp() {
        return this.app;
    }
}
