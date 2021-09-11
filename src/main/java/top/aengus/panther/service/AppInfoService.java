package top.aengus.panther.service;

import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.app.CreateAppParam;

/**
 * @author sunyongsheng (sunyongsheng@bytedance.com)
 * <p>
 * date 2021/4/13
 */
public interface AppInfoService {

    AppInfo findByAppId(String appId);

    AppDTO findDTOByAppId(String appId);

    boolean isSuperRoleApp(String appId);

    String createApp(CreateAppParam param);

    void updateAppAvatar(String appId, String avatarUrl);
}
