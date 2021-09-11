package top.aengus.panther.service.impl;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import top.aengus.panther.dao.AppInfoRepository;
import top.aengus.panther.enums.AppRole;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.event.CreateAppEvent;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.NotFoundException;
import top.aengus.panther.model.app.AppDTO;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.app.CreateAppParam;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.tool.EncryptUtil;
import top.aengus.panther.tool.StringUtil;

@Service
public class AppInfoServiceImpl implements AppInfoService {

    private final AppInfoRepository appInfoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AppInfoServiceImpl(AppInfoRepository appInfoRepository, ApplicationEventPublisher eventPublisher) {
        this.appInfoRepository = appInfoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AppInfo findByAppId(String appId) {
        return appInfoRepository.findByAppId(appId);
    }

    @Override
    public AppDTO findDTOByAppId(String appId) {
        AppInfo appInfo = appInfoRepository.findByAppId(appId);
        if (appInfo == null) {
            throw new NotFoundException("App不存在！", appId);
        }
        return convertToDto(appInfo);
    }

    @Override
    public boolean isSuperRoleApp(String appId) {
        AppInfo appInfo = appInfoRepository.findByAppId(appId);
        return appInfo != null && appInfo.isSuperRole();
    }

    @Override
    public String createApp(CreateAppParam param) {
        if (StringUtil.isEmpty(param.getEnglishName()) || param.getEnglishName().contains(" ")) {
            throw new BadRequestException("英文名不能为空或含有空格！");
        }
        appInfoRepository.findByEnglishName(param.getEnglishName()).ifPresent(app -> {
            throw new BadRequestException("英文名有重复！");
        });
        String appId = IdUtil.fastSimpleUUID();
        AppInfo appInfo = new AppInfo();
        BeanUtils.copyProperties(param, appInfo);
        appInfo.setAppId(appId);
        appInfo.setPassword(EncryptUtil.encrypt(param.getPassword()));
        appInfo.setCreateTime(System.currentTimeMillis());
        appInfo.setRole(AppRole.NORMAL.getCode());
        appInfo.setStatus(AppStatus.NORMAL.getCode());
        eventPublisher.publishEvent(new CreateAppEvent(this, appInfoRepository.save(appInfo)));
        return appId;
    }

    @Override
    public void updateAppAvatar(String appId, String avatarUrl) {
        AppInfo appInfo = appInfoRepository.findByAppId(appId);
        if (appInfo == null) {
            throw new NotFoundException("找不到App信息！", appId);
        }
        appInfo.setAvatarUrl(avatarUrl);
    }

    private AppDTO convertToDto(AppInfo appInfo) {
        AppDTO appDTO = new AppDTO();
        BeanUtils.copyProperties(appInfo, appDTO);
        appDTO.setRole(AppRole.fromCode(appInfo.getRole()));
        appDTO.setStatus(AppStatus.fromCode(appInfo.getStatus()));
        return appDTO;
    }
}
