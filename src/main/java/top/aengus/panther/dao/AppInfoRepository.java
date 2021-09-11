package top.aengus.panther.dao;

import org.springframework.data.repository.CrudRepository;
import top.aengus.panther.model.app.AppInfo;

import java.util.Optional;


public interface AppInfoRepository extends CrudRepository<AppInfo, Integer> {

    AppInfo findByAppId(String appId);

    Optional<AppInfo> findByEnglishName(String englishName);
}
