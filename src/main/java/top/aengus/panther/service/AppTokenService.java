package top.aengus.panther.service;

import top.aengus.panther.enums.TokenStage;
import top.aengus.panther.model.token.AppToken;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/26
 */
public interface AppTokenService {

    AppToken findByAppIdAndStage(Long appId, TokenStage stage);

    AppToken findByTokenAndStage(String token, TokenStage stage);

    String createOrUpdateToken(Long appId, TokenStage stage);

}
