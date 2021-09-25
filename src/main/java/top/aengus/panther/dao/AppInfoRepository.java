package top.aengus.panther.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import top.aengus.panther.model.app.AppInfo;

import java.util.Optional;


public interface AppInfoRepository extends CrudRepository<AppInfo, Integer> {

    AppInfo findByAppKey(String appKey);

    Optional<AppInfo> findByEnglishName(String englishName);

    Page<AppInfo> findAllByOwner(String owner, Pageable pageable);
}
