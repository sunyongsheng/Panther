package top.aengus.panther.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import top.aengus.panther.model.setting.AppSetting;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
@Repository
public interface AppSettingRepository extends CrudRepository<AppSetting, Long> {

    AppSetting findByAppIdAndKey(Long appId, String key);

}
