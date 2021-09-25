package top.aengus.panther.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aengus.panther.dao.AppTokenRepository;
import top.aengus.panther.enums.TokenStage;
import top.aengus.panther.exception.NotFoundException;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.token.AppToken;
import top.aengus.panther.service.AppTokenService;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/26
 */
@Service
public class AppTokenServiceImpl implements AppTokenService {

    private final AppTokenRepository appTokenRepository;

    @Autowired
    public AppTokenServiceImpl(AppTokenRepository appTokenRepository) {
        this.appTokenRepository = appTokenRepository;
    }

    @Override
    public AppToken findByAppIdAndStage(Long appId, TokenStage stage) {
        return appTokenRepository.findByAppIdAndStage(appId, stage.name());
    }

    @Override
    public AppToken findByTokenAndStage(String token, TokenStage stage) {
        return appTokenRepository.findByTokenAndStage(token, stage.name());
    }

    @Override
    public String createOrUpdateToken(Long appId, TokenStage stage) {
        String token = RandomStringUtils.random(48, true, true);
        long now = System.currentTimeMillis();

        AppToken appToken = appTokenRepository.findByAppIdAndStage(appId, stage.name());
        if (appToken == null) {
            appToken = new AppToken();
            appToken.setAppId(appId);
            appToken.setStage(stage.name());
            appToken.setCreateTime(now);
        }
        appToken.setToken(token);
        appToken.setUpdateTime(now);
        appTokenRepository.save(appToken);
        return token;
    }

}
