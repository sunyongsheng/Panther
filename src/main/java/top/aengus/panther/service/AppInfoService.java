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

    Page<AppInfo> findAllByStatus(AppStatus status, int page, int pageSize);

    AppInfo findByAppKey(String appKey);

    AppDTO findDTOByAppKey(String appKey);

    List<AppDTO> findDTOByName(String name);

    Page<AppDTO> findDTOsByOwner(String owner, int page, int pageSize);

    String createApp(CreateAppParam param, String owner);

    AppDTO updateAppInfo(String appKey, UpdateAppParam param);

    /**
     * 非{@link AppStatus#DELETED}状态App可被删除
     */
    void deleteApp(String appKey);

    /**
     * 只有{@link AppStatus#DELETED}状态App可被恢复
     */
    void undeleteApp(String appKey);

    /**
     * 只有{@link AppStatus#NORMAL}状态App可被锁定
     */
    void lockApp(String appKey);

    /**
     * 只有{@link AppStatus#LOCKED}状态App可被解锁
     */
    void unlockApp(String appKey);

    String generateUploadToken(String appKey);

    void updateAppSetting(String appKey, UpdateAppSettingParam param);

    void deleteAppForever(Long appId);

}
