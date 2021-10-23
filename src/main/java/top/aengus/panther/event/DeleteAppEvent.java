package top.aengus.panther.event;

import org.springframework.context.ApplicationEvent;
import top.aengus.panther.model.app.AppInfo;


public class DeleteAppEvent extends ApplicationEvent {

    private final AppInfo app;

    private final Boolean forever;

    public DeleteAppEvent(Object source, AppInfo app, Boolean forever) {
        super(source);
        this.app = app;
        this.forever = forever;
    }

    public AppInfo getApp() {
        return app;
    }

    public Boolean isForever() {
        return forever;
    }
}
