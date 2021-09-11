package top.aengus.panther.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.InternalException;
import top.aengus.panther.model.InstallPantherParam;
import top.aengus.panther.service.FileService;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.FileUtil;
import top.aengus.panther.tool.ImageDirUtil;
import top.aengus.panther.tool.StringUtil;
import top.aengus.panther.tool.UrlUtil;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/4
 */
@Slf4j
@Service
public class PantherConfigServiceImpl implements PantherConfigService {

    private final static String CONFIG_NAME = "panther-config.ini";
    private final static String BACKUP_CONFIG_NAME = ".panther-config.ini";

    private final static String KEY_HOST_URL = "hostUrl";
    private final static String KEY_SAVE_ROOT_PATH = "saveRootPath";

    private final Properties properties = new Properties();
    private final Properties backupProperties = new Properties();

    private String hostUrl;
    private String saveRootPath;

    private boolean hasInstalled = false;

    private final FileService fileService;

    @Autowired
    public PantherConfigServiceImpl(FileService fileService) {
        this.fileService = fileService;

        File configFile = new File(CONFIG_NAME);
        if (configFile.exists()) {
            try {
                properties.load(new BufferedInputStream(new FileInputStream(configFile)));

                hostUrl = UrlUtil.ensureNoSuffix(properties.getProperty(KEY_HOST_URL));
                if (StringUtil.isEmpty(hostUrl)) {
                    throw new RuntimeException("hostUrl can't be empty, please check " + CONFIG_NAME);
                }

                saveRootPath = FileUtil.ensureNoSuffix(properties.getProperty(KEY_SAVE_ROOT_PATH));
                if (StringUtil.isEmpty(saveRootPath)) {
                    String fallbackPath = getFallbackPath();
                    saveRootPath = fallbackPath;
                    storeProperty(KEY_SAVE_ROOT_PATH, fallbackPath);
                }

                log.info("Load Config...hostUrl=[" + hostUrl + "] saveRootPath=[" + saveRootPath + "]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File backupFile = new File(BACKUP_CONFIG_NAME);
        if (backupFile.exists()) {
            try {
                backupProperties.load(new BufferedInputStream(new FileInputStream(backupFile)));

                String backupHostUrl = backupProperties.getProperty(KEY_HOST_URL);
                if (!hostUrl.equals(UrlUtil.ensureNoSuffix(backupHostUrl)) && StringUtil.isNotEmpty(backupHostUrl)) {
                    log.warn("Detect hostUrl changed, reset the hostUrl, please change it on the admin manager.");
                    storeProperty(KEY_HOST_URL, UrlUtil.ensureNoSuffix(backupHostUrl));
                }

                String backupSaveRootPath = backupProperties.getProperty(KEY_SAVE_ROOT_PATH);
                if (!saveRootPath.equals(FileUtil.ensureNoSuffix(backupSaveRootPath)) && StringUtil.isNotEmpty(backupSaveRootPath)) {
                    log.warn("Detect saveRootPath changed, reset the saveRootPath, please change it on the admin manager.");
                    storeProperty(KEY_SAVE_ROOT_PATH, FileUtil.ensureNoSuffix(backupSaveRootPath));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getHostUrl() {
        return hostUrl;
    }

    @Override
    public String getSaveRootPath() {
        return saveRootPath;
    }

    @Override
    public void updateSaveRootPath(String saveRootPath) {
        preCheckInstall();
        saveRootPath = FileUtil.ensureNoSuffix(saveRootPath);
        File check = new File(saveRootPath);
        if (!check.exists() || !check.isDirectory()) {
            throw new BadRequestException("路径不存在或不是目录！");
        }
        this.saveRootPath = saveRootPath;
        storeProperty(KEY_SAVE_ROOT_PATH, saveRootPath);
        storeBackupProperty(KEY_SAVE_ROOT_PATH, saveRootPath);
    }

    @Override
    public void updateHostUrl(String hostUrl) {
        preCheckInstall();
        hostUrl = UrlUtil.ensureNoSuffix(hostUrl);
        this.hostUrl = hostUrl;
        storeProperty(KEY_HOST_URL, hostUrl);
        storeBackupProperty(KEY_HOST_URL, hostUrl);
    }

    @Override
    public boolean hasInstalled() {
        if (hasInstalled) {
            return true;
        }
        File backupFile = new File(BACKUP_CONFIG_NAME);
        if (backupFile.exists()) {
            try {
                backupProperties.load(new BufferedInputStream(new FileInputStream(backupFile)));
            } catch (IOException e) {
                throw new InternalException("服务器出错", e);
            }
            hasInstalled = true;
            return true;
        } else {
            hasInstalled = false;
            return false;
        }
    }

    @Override
    public boolean install(InstallPantherParam param) {
        try {
            File configFile = new File(CONFIG_NAME);
            if (configFile.exists()) {
                Properties properties = new Properties();
                properties.load(new FileInputStream(configFile));
                if (StringUtil.isNotEmpty(properties.getProperty(KEY_HOST_URL))
                        || StringUtil.isNotEmpty(properties.getProperty(KEY_SAVE_ROOT_PATH))) {
                    throw new BadRequestException("Panther已安装，请删除jar包目录下的" + CONFIG_NAME + "文件以重新安装");
                }
            } else {
                if (!configFile.createNewFile()) {
                    throw new InternalException("创建配置文件失败，请在jar包目录下手动创建" + CONFIG_NAME + "文件并再次点击安装");
                }
            }

            File backupFile = new File(BACKUP_CONFIG_NAME);
            if (backupFile.exists()) {
                if (!backupFile.delete()) {
                    throw new BadRequestException("请手动删除jar包目录下的" + BACKUP_CONFIG_NAME + "文件");
                }
            }
            backupFile.createNewFile();

            properties.load(new FileInputStream(configFile));
            backupProperties.load(new FileInputStream(backupFile));

            hasInstalled = true;

            updateHostUrl(param.getHostUrl());
            updateSaveRootPath(param.getSaveRootPath());

            fileService.initWorkspace(param.getSaveRootPath(), param.getImgDirs());

            return true;
        } catch (IOException e) {
            log.error("安装出错", e);
        }
        return false;
    }

    private void preCheckInstall() {
        if (!hasInstalled) {
            throw new BadRequestException("请先安装Panther");
        }
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

    private void storeProperty(String key, String value) {
        properties.put(key, value);
        try {
            properties.store(new FileOutputStream(CONFIG_NAME), "Save Configs File.");
        } catch (IOException e) {
            log.error("exception while storeProperty", e);
        }
    }

    private void storeBackupProperty(String key, String value) {
        backupProperties.put(key, value);
        try {
            backupProperties.store(new FileOutputStream(BACKUP_CONFIG_NAME), "Save Backup Config File");
        } catch (IOException e) {
            log.error("exception while storeBackupProperty", e);
        }
    }
}
