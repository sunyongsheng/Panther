package top.aengus.panther.dao;

import org.springframework.data.repository.CrudRepository;
import top.aengus.panther.model.config.PantherConfig;

public interface PantherConfigRepository extends CrudRepository<PantherConfig, Long> {

    PantherConfig findByConfigKey(String configKey);

}
