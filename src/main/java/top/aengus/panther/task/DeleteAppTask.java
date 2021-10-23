package top.aengus.panther.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.event.DeleteAppEvent;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.service.AppInfoService;

@Slf4j
@Configuration
@EnableScheduling
public class DeleteAppTask {

    private static final int PAGE_SIZE = 20;

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
            Page<AppInfo> appInfoPage = appInfoService.findAllByStatus(AppStatus.DELETED, currPage, PAGE_SIZE);
            appInfoPage.forEach(app -> decideDeleteApp(app, now));
            if (appInfoPage.getNumberOfElements() < PAGE_SIZE) {
                break;
            }
            currPage++;
        }
    }

    private void decideDeleteApp(AppInfo app, long deadline) {
        if (calculateDays(app.getUpdateTime(), deadline) > 30) {
            log.info("[deleteAppForever] {} 被永久删除", app);
            appInfoService.deleteAppForever(app.getId());
            eventPublisher.publishEvent(new DeleteAppEvent(this, app, true));
        }
    }

    private int calculateDays(long start, long end) {
        return (int) ((end - start) / 86400000);
    }

}
