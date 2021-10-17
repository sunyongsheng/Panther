package top.aengus.panther.service;

import org.springframework.data.domain.Page;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.app.CreateAppParam;
import top.aengus.panther.model.app.UpdateAppParam;
import top.aengus.panther.model.setting.UpdateAppSettingParam;

import java.util.List;

/**
 * @author sunyongsheng (sunyongsheng@bytedance.com)
 * <p>
 * date 2021/4/13
 */
public interface AppInfoService {

    long countAll();

    AppInfo findById(Long id);

    AppInfo findByAppKey(String appKey);

    AppDTO findDTOByAppKey(String appKey);

    List<AppDTO> findDTOByName(String name);

    Page<AppDTO> findDTOsByOwner(String owner, int page, int pageSize);

    String createApp(CreateAppParam param, String owner);

    AppDTO updateAppInfo(String appKey, UpdateAppParam param);

    void updateAppStatus(String appKey, AppStatus appStatus);

    void updateAppAvatar(String appKey, String avatarUrl);

    String generateUploadToken(String appKey);

    void updateAppSetting(String appKey, UpdateAppSettingParam param);

}
