package top.aengus.panther.dao;

import org.springframework.data.repository.CrudRepository;
import top.aengus.panther.model.token.AppToken;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/26
 */
public interface AppTokenRepository extends CrudRepository<AppToken, Long> {

    AppToken findByAppIdAndStage(Long appId, String stage);

    AppToken findByTokenAndStage(String token, String stage);
}
