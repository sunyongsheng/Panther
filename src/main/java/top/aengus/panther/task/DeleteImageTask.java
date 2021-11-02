package top.aengus.panther.task;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import top.aengus.panther.core.Constants;
import top.aengus.panther.enums.ImageStatus;
import top.aengus.panther.model.image.ImageModel;
import top.aengus.panther.service.ImageService;

import java.util.Date;

@Slf4j
@Configuration
@EnableScheduling
public class DeleteImageTask {

    private final ImageService imageService;

    @Autowired
    public DeleteImageTask(ImageService imageService) {
        this.imageService = imageService;
    }

    @Scheduled(cron = "0 0 3 1/1 * ?")
    public void deleteImageForever() {
        long now = System.currentTimeMillis();
        int currPage = 0;
        while (true) {
            Page<ImageModel> imageModels = imageService.findAllByStatus(ImageStatus.DELETED, currPage, Constants.PAGE_SIZE);
            imageModels.forEach(image -> decideDeleteImage(image, now));
            if (imageModels.getNumberOfElements() < Constants.PAGE_SIZE) {
                break;
            }
            currPage++;
        }
    }

    private void decideDeleteImage(ImageModel image, long deadline) {
        if (DateUtil.betweenDay(new Date(image.getUpdateTime()), new Date(deadline), false) > Constants.RETENTION_DAYS) {
            log.info("[deleteImageForever] {} 被永久删除", image);
            imageService.deleteImageForever(image.getId(), null, true);
        }
    }
}
