package top.aengus.panther.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.dao.ImageRepository;
import top.aengus.panther.enums.AppSettingKey;
import top.aengus.panther.enums.ImageStatus;
import top.aengus.panther.enums.NamingStrategy;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.InternalException;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.setting.AppSetting;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.model.image.ImageModel;
import top.aengus.panther.service.AppSettingService;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.service.PantherConfigService;
import top.aengus.panther.tool.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final AppSettingService appSettingService;
    private final PantherConfigService configService;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository, AppSettingService appSettingService, PantherConfigService configService) {
        this.imageRepository = imageRepository;
        this.appSettingService = appSettingService;
        this.configService = configService;
    }

    @Override
    public List<ImageDTO> findAllByAppId(String appId) {
        List<ImageModel> imageList = imageRepository.findAllByCreator(appId);
        List<ImageDTO> res = new ArrayList<>();
        for (ImageModel model : imageList) {
            res.add(convertToDto(model));
        }
        return res;
    }

    @Override
    public ImageModel findImageByName(String filename) {
        return imageRepository.findBySaveName(filename);
    }

    @Override
    public ImageDTO saveImage(MultipartFile image, String dir, AppInfo appInfo) {
        if (image.isEmpty()) {
            throw new BadRequestException("文件不能为空！");
        }

        String originName = image.getOriginalFilename();
        if (!FileUtil.isPic(originName)) {
            throw new BadRequestException("非图片文件！");
        }
        AppSetting setting = appSettingService.findAppSetting(appInfo.getId(), AppSettingKey.IMG_NAMING_STRATEGY.getCode());
        NamingStrategy rule;
        if (setting == null) {
            rule = NamingStrategy.UUID;
        } else {
            rule = NamingStrategy.valueOf(setting.getValue());
        }
        String saveName = generateName(rule, originName);
        String absolutePath = generateAbsolutePath(appInfo, dir, saveName);
        String relativePath = generateRelativePath(appInfo, dir, saveName);
        ImageModel imageModel = new ImageModel();
        imageModel.setOriginalName(originName);
        imageModel.setSaveName(saveName);
        imageModel.setAbsolutePath(absolutePath);
        imageModel.setRelativePath(relativePath);
        imageModel.setUrl(generateUrl(relativePath));
        imageModel.setUploadTime(System.currentTimeMillis());
        imageModel.setCreator(appInfo.getAppId());
        imageModel.setSize(image.getSize());
        imageModel.setStatus(ImageStatus.NORMAL.getCode());
        try {
            File dest = new File(absolutePath);
            FileUtil.checkAndCreateDir(dest.getParentFile());
            image.transferTo(dest);
            log.info("上传图片成功：name={}, namingRule={}", dest.getName(), rule.getDesc());
            return convertToDto(imageRepository.save(imageModel));
        } catch (IOException e) {
            throw new InternalException("保存图片异常", e);
        }
    }

    private String generateAbsolutePath(AppInfo app, String dir, String name) {
        if (!ImageDirUtil.isValidDir(dir)) {
            throw new BadRequestException("路径不合法，只能为 common|post|travel");
        }
        if (app.isSuperRole() && StringUtil.isNotEmpty(dir)) {
            return new File(new File(configService.getSaveRootPath(), dir), name).getAbsolutePath();
        } else {
            File appRoot = new File(configService.getSaveRootPath(), ImageDirUtil.NAME_APP);
            File appSpecial = new File(appRoot, app.getEnglishName());
            return new File(appSpecial, name).getAbsolutePath();
        }
    }

    private String generateRelativePath(AppInfo app, String dir, String name) {
        if (app.isSuperRole() && StringUtil.isNotEmpty(dir)) {
            return FileUtil.ensureSuffix(FileUtil.ensurePrefix(dir)) + name;
        } else {
            return FileUtil.FILE_SEPARATOR + ImageDirUtil.NAME_APP +
                    FileUtil.FILE_SEPARATOR + app.getEnglishName() +
                    FileUtil.FILE_SEPARATOR + name;
        }
    }

    private String generateName(NamingStrategy rule, String originName) {
        String extension = FileUtil.getExtension(originName);
        switch (rule) {
            case UUID:
                return IdUtil.fastSimpleUUID() + extension;
            case ORIGIN:
                return originName;
            case DATE_UUID_HYPHEN:
                return DateUtil.today() + "-" + IdUtil.fastSimpleUUID() + extension;
            case DATE_ORIGIN_HYPHEN:
                return DateUtil.today() + "-" + originName;
            case DATE_UUID_UNDERLINE:
                return DateFormatter.dateUnderlineFormat(new Date()) + "_" + IdUtil.fastSimpleUUID() + extension;
            case DATE_ORIGIN_UNDERLINE:
                return DateFormatter.dateUnderlineFormat(new Date()) + "_" + originName;
        }
        return generateName(NamingStrategy.DATE_UUID_HYPHEN, originName);
    }

    private String generateUrl(String relativePath) {
        return UrlUtil.ensureNoSuffix(configService.getHostUrl()) + relativePath;
    }

    @Override
    public boolean deleteImage(Long imageId, String operation) {
        ImageModel original = imageRepository.findByIdAndCreator(imageId, operation);
        if (original == null) {
            return false;
        }
        original.delete();
        imageRepository.save(original);
        return true;
    }

    private ImageDTO convertToDto(ImageModel imageModel) {
        ImageDTO dto = new ImageDTO();
        dto.setId(imageModel.getId());
        dto.setName(imageModel.getSaveName());
        dto.setUrl(imageModel.getUrl());
        dto.setUploadTime(imageModel.getUploadTime());
        dto.setCreator(imageModel.getCreator());
        dto.setSize(imageModel.getSize());
        return dto;
    }
}
