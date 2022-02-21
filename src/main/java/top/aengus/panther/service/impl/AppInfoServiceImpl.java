package top.aengus.panther.service.impl;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import top.aengus.panther.core.Constants;
import top.aengus.panther.dao.AppInfoRepository;
import top.aengus.panther.enums.*;
import top.aengus.panther.event.CreateAppEvent;
import top.aengus.panther.event.DeleteAppEvent;
import top.aengus.panther.event.UndeleteAppEvent;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.NotFoundException;
import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.app.CreateAppParam;
import top.aengus.panther.model.app.UpdateAppParam;
import top.aengus.panther.model.setting.UpdateAppSettingParam;
import top.aengus.panther.model.token.AppToken;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.AppSettingService;
import top.aengus.panther.service.AppTokenService;
import top.aengus.panther.tool.FileUtil;
import top.aengus.panther.tool.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppInfoServiceImpl implements AppInfoService {

    private final AppInfoRepository appInfoRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final AppTokenService appTokenService;
    private final AppSettingService appSettingService;

    @Autowired
    public AppInfoServiceImpl(AppInfoRepository appInfoRepository, ApplicationEventPublisher eventPublisher, AppTokenService appTokenService, AppSettingService appSettingService) {
        this.appInfoRepository = appInfoRepository;
        this.eventPublisher = eventPublisher;
        this.appTokenService = appTokenService;
        this.appSettingService = appSettingService;
    }

    @Override
    public long countAll() {
        return appInfoRepository.count();
    }

    @Override
    public Iterable<AppInfo> findAll() {
        return appInfoRepository.findAll();
    }

    @Override
    public Page<AppInfo> findAllByStatus(AppStatus status, int page, int pageSize) {
        Pageable request = PageRequest.of(page, pageSize);
        return appInfoRepository.findAllByStatus(status.getCode(), request);
    }

    @Override
    @NonNull
    public AppInfo findByAppKey(String appKey) {
        if (appKey.equals(Constants.UNKNOWN_APP_KEY)) return AppInfo.empty();
        return appInfoRepository.findByAppKey(appKey);
    }

    @Override
    public AppDTO findDTOByAppKey(String appKey) {
        AppInfo appInfo = findAppWithCheck(appKey, false);
        return convertToDto(appInfo);
    }

    @Override
    @NonNull
    public AppInfo findByEnglishName(String name) {
        return appInfoRepository.findByEnglishName(name).orElse(AppInfo.empty());
    }

    @Override
    public List<AppDTO> searchDTOByName(String name) {
        List<AppInfo> appInfo = appInfoRepository.findByNameContainsOrEnglishNameContains(name, name);
        if (appInfo == null) return Collections.emptyList();
        return appInfo.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public Page<AppDTO> findDTOsByOwner(String owner, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return appInfoRepository.findAllByOwner(owner, pageable).map(this::convertToDto);
    }

    @Override
    public String createApp(CreateAppParam param, String owner) {
        if (StringUtil.isEmpty(param.getEnglishName()) || param.getEnglishName().contains(" ")) {
            throw new BadRequestException("英文名不能为空或含有空格！");
        }
        if (param.getEnglishName().contains("/") || FileUtil.isDirnameIllegal(param.getEnglishName())) {
            throw new BadRequestException("英文名不能包含特殊符号！");
        }
        if (Constants.UNKNOWN_APP_KEY.equals(param.getEnglishName())) {
            throw new BadRequestException("英文名非法！");
        }
        if (Constants.UNKNOWN_APP_NAME.equals(param.getName())) {
            throw new BadRequestException("App名称非法！");
        }
        appInfoRepository.findByEnglishName(param.getEnglishName()).ifPresent(app -> {
            throw new BadRequestException("英文名有重复！");
        });
        String appKey = IdUtil.fastSimpleUUID();
        AppInfo appInfo = new AppInfo();
        BeanUtils.copyProperties(param, appInfo);
        appInfo.setAppKey(appKey);
        appInfo.setOwner(owner);
        appInfo.setCreateTime(System.currentTimeMillis());
        appInfo.setRole(param.getAppRole().getCode());
        appInfo.setStatus(AppStatus.NORMAL.getCode());
        eventPublisher.publishEvent(new CreateAppEvent(this, appInfoRepository.save(appInfo)));
        return appKey;
    }

    @Override
    public AppDTO updateAppInfo(String appKey, UpdateAppParam param) {
        AppInfo appInfo = findAppWithCheck(appKey, true);
        appInfo.setName(param.getName());
        appInfo.setRole(param.getRole().getCode());
        appInfo.setUpdateTime(System.currentTimeMillis());
        return convertToDto(appInfoRepository.save(appInfo));
    }

    @Override
    public void deleteApp(String appKey) {
        AppInfo appInfo = findAppWithCheck(appKey, false);
        if (AppStatus.DELETED == AppStatus.fromCode(appInfo.getStatus())) {
            throw new BadRequestException("App已被删除！");
        }
        appInfo.setStatus(AppStatus.DELETED.getCode());
        appInfo.setUpdateTime(System.currentTimeMillis());
        eventPublisher.publishEvent(new DeleteAppEvent(this, appInfo, false));
        appInfoRepository.save(appInfo);
    }

    @Override
    public void undeleteApp(String appKey) {
        AppInfo appInfo = findAppWithCheck(appKey, false);
        if (AppStatus.DELETED != AppStatus.fromCode(appInfo.getStatus())) {
            throw new BadRequestException("App未被删除，无法恢复！");
        }
        appInfo.setStatus(AppStatus.NORMAL.getCode());
        appInfo.setUpdateTime(System.currentTimeMillis());
        eventPublisher.publishEvent(new UndeleteAppEvent(this, appInfo));
        appInfoRepository.save(appInfo);
    }

    @Override
    public void lockApp(String appKey) {
        AppInfo appInfo = findAppWithCheck(appKey, false);
        if (AppStatus.NORMAL != AppStatus.fromCode(appInfo.getStatus())) {
            throw new BadRequestException("App状态不正确！");
        }
        appInfo.setStatus(AppStatus.LOCKED.getCode());
        appInfo.setUpdateTime(System.currentTimeMillis());
        appInfoRepository.save(appInfo);
    }

    @Override
    public void unlockApp(String appKey) {
        AppInfo appInfo = findAppWithCheck(appKey, false);
        if (AppStatus.LOCKED != AppStatus.fromCode(appInfo.getStatus())) {
            throw new BadRequestException("App未被锁定，无法解锁！");
        }
        appInfo.setStatus(AppStatus.NORMAL.getCode());
        appInfo.setUpdateTime(System.currentTimeMillis());
        appInfoRepository.save(appInfo);
    }

    @Override
    public String generateUploadToken(String appKey) {
        findAppWithCheck(appKey, true);
        return appTokenService.generateToken(appKey, TokenStage.UPLOAD_V1_1);
    }

    @Override
    public void updateAppSetting(String appKey, UpdateAppSettingParam param) {
        AppInfo appInfo = findAppWithCheck(appKey, true);
        appSettingService.updateAppSetting(appInfo, param);
    }

    @Override
    public void deleteAppForever(Long appId) {
        appInfoRepository.deleteById(appId);
    }

    private AppDTO convertToDto(@NonNull AppInfo appInfo) {
        AppDTO appDTO = new AppDTO();
        BeanUtils.copyProperties(appInfo, appDTO);
        appDTO.setRole(AppRole.fromCode(appInfo.getRole()));
        appDTO.setStatus(AppStatus.fromCode(appInfo.getStatus()));

        AppToken appToken = appTokenService.findByAppKeyAndStage(appInfo.getAppKey(), TokenStage.UPLOAD_V1_1);
        appDTO.setHasUploadToken1(appToken != null);
        appDTO.setUpdateToken1GenTime(appToken == null ? 0 : appToken.getGenerateTime());

        String namingStrategy = appSettingService.findAppSettingValue(appInfo, AppSettingKey.IMG_NAMING_STRATEGY);
        appDTO.setNamingStrategy(NamingStrategy.valueOf(namingStrategy));

        String defaultSaveDir = appSettingService.findAppSettingValue(appInfo, AppSettingKey.DEFAULT_SAVE_DIR);
        appDTO.setDefaultSaveDir(defaultSaveDir);

        return appDTO;
    }

    private AppInfo findAppWithCheck(String appKey, boolean checkStatus) {
        if (StringUtil.isEmpty(appKey)) {
            throw new BadRequestException("AppKey为空！");
        }
        AppInfo appInfo = appInfoRepository.findByAppKey(appKey);
        if (appInfo == null) {
            throw new NotFoundException("App不存在！", appKey);
        }
        if (checkStatus) {
            AppStatus appStatus = AppStatus.fromCode(appInfo.getStatus());
            if (appStatus == AppStatus.LOCKED) {
                throw new BadRequestException("App已被锁定！请联系管理员");
            } else if (appStatus == AppStatus.DELETED) {
                throw new BadRequestException("App已被删除！请联系管理员");
            }
        }
        return appInfo;
    }
}
