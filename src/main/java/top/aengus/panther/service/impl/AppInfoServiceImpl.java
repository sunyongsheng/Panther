package top.aengus.panther.service.impl;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import top.aengus.panther.dao.AppInfoRepository;
import top.aengus.panther.enums.AppRole;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.enums.TokenStage;
import top.aengus.panther.event.CreateAppEvent;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.NotFoundException;
import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.app.CreateAppParam;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.AppTokenService;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.StringUtil;

@Service
public class AppInfoServiceImpl implements AppInfoService {

    private final AppInfoRepository appInfoRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final PantherConfigService pantherConfigService;
    private final AppTokenService appTokenService;

    @Autowired
    public AppInfoServiceImpl(AppInfoRepository appInfoRepository, ApplicationEventPublisher eventPublisher, PantherConfigService pantherConfigService, AppTokenService appTokenService) {
        this.appInfoRepository = appInfoRepository;
        this.eventPublisher = eventPublisher;
        this.pantherConfigService = pantherConfigService;
        this.appTokenService = appTokenService;
    }

    @Override
    public AppInfo findById(Long id) {
        return appInfoRepository.findById(id).orElse(null);
    }

    @Override
    public AppInfo findByAppKey(String appKey) {
        return appInfoRepository.findByAppKey(appKey);
    }

    @Override
    public AppDTO findDTOByAppKey(String appKey) {
        AppInfo appInfo = appInfoRepository.findByAppKey(appKey);
        if (appInfo == null) {
            throw new NotFoundException("App不存在！", appKey);
        }
        return convertToDto(appInfo);
    }

    @Override
    public Page<AppDTO> findDTOsByOwner(String owner, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return appInfoRepository.findAllByOwner(owner, pageable).map(this::convertToDto);
    }

    @Override
    public String createApp(CreateAppParam param) {
        if (StringUtil.isEmpty(param.getEnglishName()) || param.getEnglishName().contains(" ")) {
            throw new BadRequestException("英文名不能为空或含有空格！");
        }
        appInfoRepository.findByEnglishName(param.getEnglishName()).ifPresent(app -> {
            throw new BadRequestException("英文名有重复！");
        });
        String appKey = IdUtil.fastSimpleUUID();
        AppInfo appInfo = new AppInfo();
        BeanUtils.copyProperties(param, appInfo);
        appInfo.setAppKey(appKey);
        appInfo.setOwner(pantherConfigService.getAdminUsername());
        appInfo.setCreateTime(System.currentTimeMillis());
        appInfo.setRole(AppRole.NORMAL.getCode());
        appInfo.setStatus(AppStatus.NORMAL.getCode());
        eventPublisher.publishEvent(new CreateAppEvent(this, appInfoRepository.save(appInfo)));
        return appKey;
    }

    @Override
    public void updateAppAvatar(String appKey, String avatarUrl) {
        AppInfo appInfo = appInfoRepository.findByAppKey(appKey);
        if (appInfo == null) {
            throw new NotFoundException("找不到App信息！", appKey);
        }
        appInfo.setAvatarUrl(avatarUrl);
    }

    @Override
    public String generateUploadToken(String appKey) {
        AppInfo appInfo = appInfoRepository.findByAppKey(appKey);
        if (appInfo == null) {
            throw new NotFoundException("App不存在！", appKey);
        }
        return appTokenService.createOrUpdateToken(appInfo.getId(), TokenStage.UPLOAD_V1);
    }

    private AppDTO convertToDto(AppInfo appInfo) {
        AppDTO appDTO = new AppDTO();
        BeanUtils.copyProperties(appInfo, appDTO);
        appDTO.setRole(AppRole.fromCode(appInfo.getRole()));
        appDTO.setStatus(AppStatus.fromCode(appInfo.getStatus()));
        return appDTO;
    }
}
