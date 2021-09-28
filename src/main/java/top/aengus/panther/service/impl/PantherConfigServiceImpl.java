package top.aengus.panther.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import top.aengus.panther.dao.PantherConfigRepository;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.InternalException;
import top.aengus.panther.model.InstallPantherParam;
import top.aengus.panther.model.config.PantherConfig;
import top.aengus.panther.service.FileService;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.*;

import java.io.*;
import java.net.URL;
import java.util.Objects;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/4
 */
@Slf4j
@Service
public class PantherConfigServiceImpl implements PantherConfigService {

    private static final String KEY_HOST_URL = "host_url";
    private static final String KEY_SAVE_ROOT_PATH = "save_root_path";
    private static final String KEY_ADMIN_USERNAME = "admin_username";
    private static final String KEY_ADMIN_PASSWORD = "admin_password";
    private static final String KEY_INSTALL_TIME = "install_time";

    private volatile String hostUrl;
    private volatile String saveRootPath;
    private volatile String adminUsername;

    private volatile boolean hasInstalled = false;

    private final PantherConfigRepository pantherConfigRepository;
    private final FileService fileService;

    @Autowired
    public PantherConfigServiceImpl(PantherConfigRepository pantherConfigRepository, FileService fileService) {
        this.pantherConfigRepository = pantherConfigRepository;
        this.fileService = fileService;
        hasInstalled();
    }

    @Override
    public String getHostUrl() {
        preCheckInstall();
        if (hostUrl == null) {
            synchronized (this) {
                if (hostUrl == null) {
                    PantherConfig config = pantherConfigRepository.findByConfigKey(KEY_HOST_URL);
                    if (config == null) {
                        throw new InternalException("出现异常， 请检查数据库");
                    }
                    hostUrl = config.getConfigValue();
                }
            }
        }
        return hostUrl;
    }

    @Override
    public String getSaveRootPath() {
        preCheckInstall();
        if (saveRootPath == null) {
            synchronized (this) {
                if (saveRootPath == null) {
                    PantherConfig config = pantherConfigRepository.findByConfigKey(KEY_SAVE_ROOT_PATH);
                    if (config == null) {
                        throw new InternalException("出现异常，请检查数据库");
                    }
                    saveRootPath = config.getConfigValue();
                }
            }
        }
        return saveRootPath;
    }

    @Override
    public String getAdminUsername() {
        preCheckInstall();
        if (adminUsername == null) {
            synchronized (this) {
                if (adminUsername == null) {
                    PantherConfig config = pantherConfigRepository.findByConfigKey(KEY_ADMIN_USERNAME);
                    if (config == null) {
                        throw new InternalException("出现异常，请检查数据库");
                    }
                    adminUsername = config.getConfigValue();
                }
            }
        }
        return adminUsername;
    }

    @Override
    public String getAdminPassword() {
        preCheckInstall();
        return pantherConfigRepository.findByConfigKey(KEY_ADMIN_PASSWORD).getConfigValue();
    }

    @Override
    public String getRunningDuration() {
        PantherConfig config = pantherConfigRepository.findByConfigKey(KEY_INSTALL_TIME);
        if (config == null) {
            return "未安装";
        }
        long duration = (System.currentTimeMillis() - Long.parseLong(config.getConfigValue())) / 1000;
        if (duration < 60) {
            return duration + "秒";
        } else if (duration < 3600) {
            long minutes = duration / 60;
            return minutes + "分" + (duration - minutes * 60) + "秒";
        } else if (duration < 86400) {
            long hours = duration / 3600;
            long minutes = (duration - hours * 3600) / 60;
            return hours + "时" + minutes + "分";
        } else {
            long days = duration / 86400;
            long hours = (duration - days * 86400) / 3600;
            return days + "天" + hours + "时";
        }
    }

    @Override
    public void updateSaveRootPath(String saveRootPath) {
        preCheckInstall();
        saveRootPath = FileUtil.ensureNoSuffix(saveRootPath);
        PantherConfig config = findConfigWithCheck(KEY_SAVE_ROOT_PATH, saveRootPath);
        if (config != null) {
            File check = new File(saveRootPath);
            if (!check.exists() || !check.isDirectory()) {
                throw new BadRequestException("路径不存在或不是目录！");
            }
            config.setConfigValue(saveRootPath);
            config.setUpdateTime(System.currentTimeMillis());
            pantherConfigRepository.save(config);
            this.saveRootPath = saveRootPath;
        }
    }

    @Override
    public void updateHostUrl(String hostUrl) {
        preCheckInstall();
        hostUrl = UrlUtil.ensureNoSuffix(hostUrl);
        PantherConfig config = findConfigWithCheck(KEY_HOST_URL, hostUrl);
        if (config != null) {
            config.setConfigValue(hostUrl);
            config.setUpdateTime(System.currentTimeMillis());
            pantherConfigRepository.save(config);
            this.hostUrl = hostUrl;
        }
    }

    @Override
    public void updateAdminUsername(String superAdminUsername) {
        preCheckInstall();
        PantherConfig config = findConfigWithCheck(KEY_ADMIN_USERNAME, superAdminUsername);
        if (config != null) {
            config.setConfigValue(superAdminUsername);
            config.setUpdateTime(System.currentTimeMillis());
            pantherConfigRepository.save(config);
        }
    }

    @Override
    public void updateAdminPassword(String superAdminPassword) {
        preCheckInstall();
        superAdminPassword = EncryptUtil.encrypt(superAdminPassword);
        PantherConfig config = findConfigWithCheck(KEY_ADMIN_PASSWORD, superAdminPassword);
        if (config != null) {
            config.setConfigValue(superAdminPassword);
            config.setUpdateTime(System.currentTimeMillis());
            pantherConfigRepository.save(config);
        }
    }

    @Override
    public boolean hasInstalled() {
        if (hasInstalled) {
            return true;
        }
        PantherConfig pantherConfig = pantherConfigRepository.findByConfigKey(KEY_INSTALL_TIME);
        if (pantherConfig == null) {
            hasInstalled = false;
            return false;
        }
        long installTime = Long.parseLong(pantherConfig.getConfigValue());
        if (installTime >= System.currentTimeMillis()) {
            hasInstalled = false;
            return false;
        }
        hasInstalled = true;
        return true;
    }

    @Override
    public boolean install(InstallPantherParam param) {
        if (hasInstalled()) {
            throw new BadRequestException("Panther已安装，请在超级管理员页面进行重新安装");
        }

        try {
            if (StringUtil.isEmpty(param.getSaveRootPath())) {
                param.setSaveRootPath(getFallbackPath());
            }
            File check = new File(param.getSaveRootPath());
            if (!check.exists() || !check.isDirectory()) {
                throw new BadRequestException("路径不存在或不是目录！");
            }

            long currTime = System.currentTimeMillis();
            PantherConfig hostUrlConfig = new PantherConfig();
            hostUrlConfig.setConfigKey(KEY_HOST_URL);
            hostUrlConfig.setConfigValue(UrlUtil.ensureNoSuffix(param.getHostUrl()));
            hostUrlConfig.setUpdateTime(currTime);
            pantherConfigRepository.save(hostUrlConfig);

            PantherConfig saveRootPathConfig = new PantherConfig();
            saveRootPathConfig.setConfigKey(KEY_SAVE_ROOT_PATH);
            saveRootPathConfig.setConfigValue(FileUtil.ensureNoSuffix(param.getSaveRootPath()));
            saveRootPathConfig.setUpdateTime(currTime);
            pantherConfigRepository.save(saveRootPathConfig);

            PantherConfig adminUsernameConfig = new PantherConfig();
            adminUsernameConfig.setConfigKey(KEY_ADMIN_USERNAME);
            adminUsernameConfig.setConfigValue(param.getAdminUsername());
            adminUsernameConfig.setUpdateTime(currTime);
            pantherConfigRepository.save(adminUsernameConfig);

            PantherConfig adminPassword = new PantherConfig();
            adminPassword.setConfigKey(KEY_ADMIN_PASSWORD);
            adminPassword.setConfigValue(EncryptUtil.encrypt(param.getAdminPassword()));
            adminPassword.setUpdateTime(currTime);
            pantherConfigRepository.save(adminPassword);

            PantherConfig installTimeConfig = new PantherConfig();
            installTimeConfig.setConfigKey(KEY_INSTALL_TIME);
            installTimeConfig.setConfigValue(String.valueOf(currTime));
            installTimeConfig.setUpdateTime(currTime);
            pantherConfigRepository.save(installTimeConfig);

            fileService.initWorkspace(param.getSaveRootPath(), param.getImgDirs());

            hasInstalled = true;
            return true;
        } catch (Exception e) {
            if (e instanceof BadRequestException) {
                throw e;
            }
            log.error("安装出现异常", e);
        }
        return false;
    }

    private void preCheckInstall() {
        if (!hasInstalled) {
            throw new BadRequestException("请先安装Panther");
        }
    }

    private PantherConfig findConfigWithCheck(String configKey, String originalValue) {
        PantherConfig config = pantherConfigRepository.findByConfigKey(configKey);
        if (config == null) {
            throw new BadRequestException("出现错误，请重新安装");
        }
        if (originalValue.equals(config.getConfigValue())) {
            return null;
        }
        return config;
    }

    /**
     * 获取兜底路径，运行目录/uploadImages -> 用户目录/panther/uploadImages
     */
    private String getFallbackPath() {
        final String uploadDirName = "uploadImages";
        URL resource = Objects.requireNonNull(ClassUtils.getDefaultClassLoader()).getResource("");
        if (resource == null) {
            String userHome = System.getProperty("user.home");
            File pantherDir = new File(userHome, "panther");
            FileUtil.checkAndCreateDir(pantherDir);
            File imagesDir = new File(pantherDir, uploadDirName);
            FileUtil.checkAndCreateDir(imagesDir);
            return imagesDir.getAbsolutePath();
        } else {
            File imagesDir = new File(resource.getPath(), uploadDirName);
            FileUtil.checkAndCreateDir(imagesDir);
            return imagesDir.getAbsolutePath();
        }
    }

}
