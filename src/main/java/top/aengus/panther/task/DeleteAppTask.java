package top.aengus.panther.task;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import top.aengus.panther.core.Constants;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.event.DeleteAppEvent;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.service.AppInfoService;

import java.util.Date;

@Slf4j
@Configuration
@EnableScheduling
public class DeleteAppTask {

    private final AppInfoService appInfoService;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public DeleteAppTask(AppInfoService appInfoService, ApplicationEventPublisher eventPublisher) {
        this.appInfoService = appInfoService;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "0 0 3 1/1 * ?")
    void deleteAppForever() {
        long now = System.currentTimeMillis();
        int currPage = 0;
        while (true) {
            Page<AppInfo> appInfoPage = appInfoService.findAllByStatus(AppStatus.DELETED, currPage, Constants.PAGE_SIZE);
            appInfoPage.forEach(app -> decideDeleteApp(app, now));
            if (appInfoPage.getNumberOfElements() < Constants.PAGE_SIZE) {
                break;
            }
            currPage++;
        }
    }

    private void decideDeleteApp(AppInfo app, long deadline) {
        if (DateUtil.betweenDay(new Date(app.getUpdateTime()), new Date(deadline), false) > Constants.RETENTION_DAYS) {
            log.info("[deleteAppForever] {} 被永久删除", app);
            appInfoService.deleteAppForever(app.getId());
            eventPublisher.publishEvent(new DeleteAppEvent(this, app, true));
        }
    }

}
