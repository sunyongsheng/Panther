package top.aengus.panther.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import top.aengus.panther.model.app.AppInfo;

import java.util.List;
import java.util.Optional;


public interface AppInfoRepository extends CrudRepository<AppInfo, Long> {

    AppInfo findByAppKey(String appKey);

    List<AppInfo> findByNameContainsOrEnglishNameContains(String query1, String query2);

    Optional<AppInfo> findByEnglishName(String englishName);

    Page<AppInfo> findAllByOwner(String owner, Pageable pageable);

    Page<AppInfo> findAllByStatus(Integer status, Pageable pageable);
}
