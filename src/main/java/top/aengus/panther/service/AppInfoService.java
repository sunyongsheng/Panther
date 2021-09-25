package top.aengus.panther.service;

import org.springframework.data.domain.Page;
import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.app.CreateAppParam;

/**
 * @author sunyongsheng (sunyongsheng@bytedance.com)
 * <p>
 * date 2021/4/13
 */
public interface AppInfoService {

    AppInfo findById(Long id);

    AppInfo findByAppKey(String appKey);

    AppDTO findDTOByAppKey(String appKey);

    Page<AppDTO> findDTOsByOwner(String owner, int page, int pageSize);

    String createApp(CreateAppParam param);

    void updateAppAvatar(String appKey, String avatarUrl);

    String generateUploadToken(String appKey);

}
