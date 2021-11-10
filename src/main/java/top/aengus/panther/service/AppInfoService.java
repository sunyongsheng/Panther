package top.aengus.panther.service;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.enums.TokenStage;
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

    /**
     * @return App总数量
     */
    long countAll();

    /**
     * @return 所有的App
     */
    Iterable<AppInfo> findAll();

    /**
     * 分页查询App
     *
     * @param status App的状态
     * @param page 页数，从0开始
     * @param pageSize 每次查询的数量
     * @return 所有状态为[status]的App
     */
    Page<AppInfo> findAllByStatus(AppStatus status, int page, int pageSize);

    /**
     * 查询 AppInfo#appKey == appKey的App信息
     *
     * @return 查询到的App信息，否则是{@code AppInfo#empty()}
     */
    @NonNull
    AppInfo findByAppKey(String appKey);

    /**
     * 查询 AppInfo#englishName == name的App信息
     *
     * @return 查询到的App信息，否则是{@code AppInfo#empty()}
     */
    @NonNull
    AppInfo findByEnglishName(String name);

    /**
     * 查询 AppInfo#appKey == appKey的信息
     *
     * @return 查询到的AppDTO，否则抛出NotFoundException
     * @throws top.aengus.panther.exception.NotFoundException 指定AppKey不存在
     */
    AppDTO findDTOByAppKey(String appKey);

    /**
     * 查询所有名称中包含 name 的AppDTO
     *
     * @return 名称中包含[name]的AppDTO，否则返回{@code Collections.emptyList()}
     */
    List<AppDTO> searchDTOByName(String name);

    /**
     * 查询所有AppInfo#owner == owner的AppDTO
     *
     * @return owner为[owner]的AppDTO，否则返回{@code Collections.emptyList()}
     */
    Page<AppDTO> findDTOsByOwner(String owner, int page, int pageSize);

    /**
     * 创建新App
     *
     * @param param 创建App参数
     * @param owner App所有者，目前为超级管理员
     * @return AppKey
     * @throws top.aengus.panther.exception.BadRequestException 名称或英文名有重复或者不合法
     */
    String createApp(CreateAppParam param, String owner);

    /**
     * 更新 AppInfo#status == {@link AppStatus#NORMAL)} 的App信息，否则抛出异常
     *
     * @param appKey 指定App的appKey
     * @param param 更新App参数
     * @return 更新完成后的AppDTO
     * @throws top.aengus.panther.exception.NotFoundException 指定AppKey不存在
     * @throws top.aengus.panther.exception.BadRequestException appKey为空；App被锁定或被删除
     */
    AppDTO updateAppInfo(String appKey, UpdateAppParam param);

    /**
     * 逻辑删除App，只有非{@link AppStatus#DELETED}状态的App可被删除
     *
     * @throws top.aengus.panther.exception.NotFoundException 指定AppKey不存在
     * @throws top.aengus.panther.exception.BadRequestException appKey为空或App已被删除
     */
    void deleteApp(String appKey);

    /**
     * 逻辑恢复App，只有{@link AppStatus#DELETED}状态的App可被恢复
     *
     * @throws top.aengus.panther.exception.NotFoundException 指定AppKey不存在
     * @throws top.aengus.panther.exception.BadRequestException appKey为空或App不是已删除状态
     */
    void undeleteApp(String appKey);

    /**
     * 锁定App，只有{@link AppStatus#NORMAL}状态App可被锁定
     *
     * @throws top.aengus.panther.exception.NotFoundException 指定AppKey不存在
     * @throws top.aengus.panther.exception.BadRequestException appKey为空或App不是正常状态
     */
    void lockApp(String appKey);

    /**
     * 解锁只有{@link AppStatus#LOCKED}状态App可被解锁
     *
     * @throws top.aengus.panther.exception.NotFoundException 指定AppKey不存在
     * @throws top.aengus.panther.exception.BadRequestException appKey为空或App不是锁定状态
     */
    void unlockApp(String appKey);

    /**
     * 为指定App生成上传Token，场景为{@link TokenStage#UPLOAD_V1_1}
     *
     * @param appKey 指定App的appKey
     * @return 48位Token
     * @throws top.aengus.panther.exception.NotFoundException 指定AppKey不存在
     * @throws top.aengus.panther.exception.BadRequestException appKey为空；App被锁定或被删除
     */
    String generateUploadToken(String appKey);

    /**
     * 更新指定App的设置
     *
     * @param appKey 指定App的appKey
     * @throws top.aengus.panther.exception.NotFoundException 指定AppKey不存在
     * @throws top.aengus.panther.exception.BadRequestException appKey为空；App被锁定或被删除
     */
    void updateAppSetting(String appKey, UpdateAppSettingParam param);

    /**
     * 永久删除App
     *
     * @param appId App的Id
     */
    void deleteAppForever(Long appId);

}
