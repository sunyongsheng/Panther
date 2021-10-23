package top.aengus.panther.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.dao.ImageRepository;
import top.aengus.panther.enums.AppSettingKey;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.enums.ImageStatus;
import top.aengus.panther.enums.NamingStrategy;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.NotFoundException;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.setting.AppSetting;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.model.image.ImageModel;
import top.aengus.panther.service.*;
import top.aengus.panther.tool.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    private final AppInfoService appInfoService;
    private final AppSettingService appSettingService;
    private final PantherConfigService configService;
    private final FileService fileService;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository, AppInfoService appInfoService, AppSettingService appSettingService, PantherConfigService configService, FileService fileService) {
        this.imageRepository = imageRepository;
        this.appInfoService = appInfoService;
        this.appSettingService = appSettingService;
        this.configService = configService;
        this.fileService = fileService;
    }

    @Override
    public long countAll() {
        return imageRepository.count();
    }

    @Override
    public long countByAppKey(String appKey) {
        return imageRepository.countAllByOwner(appKey);
    }

    @Override
    public Page<ImageDTO> findAll(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return imageRepository.findAll(pageRequest).map(this::convertToDto);
    }

    @Override
    public List<ImageDTO> findAllByAppKey(String appKey) {
        List<ImageModel> imageList = imageRepository.findAllByOwner(appKey);
        List<ImageDTO> res = new ArrayList<>();
        for (ImageModel model : imageList) {
            res.add(convertToDto(model));
        }
        return res;
    }

    @Override
    public ImageDTO saveImage(MultipartFile image, String dir, String appKey, boolean isAdmin) {
        if (image.isEmpty()) {
            throw new BadRequestException("文件不能为空！");
        }

        String originName = image.getOriginalFilename();
        if (!FileUtil.isPic(originName)) {
            throw new BadRequestException(originName + " 非图片文件！");
        }

        String saveDir = tryCheckDir(dir);

        AppInfo appInfo = appInfoService.findByAppKey(appKey);
        if (appInfo == null) {
            throw new NotFoundException("App不存在！请先创建App", appKey);
        }
        AppStatus appStatus = AppStatus.fromCode(appInfo.getStatus());
        if (appStatus == AppStatus.LOCKED) {
            throw new BadRequestException("App已被锁定！请联系管理员");
        } else if (appStatus == AppStatus.DELETED) {
            throw new BadRequestException("App已被删除！请联系管理员");
        }

        AppSetting setting = appSettingService.findAppSetting(appInfo.getId(), AppSettingKey.IMG_NAMING_STRATEGY.getCode());
        NamingStrategy rule;
        if (setting == null) {
            rule = NamingStrategy.UUID;
        } else {
            rule = NamingStrategy.valueOf(setting.getValue());
        }
        String saveName = generateName(rule, originName);
        String absolutePath = generateAbsolutePath(appInfo, saveDir, saveName);
        String relativePath = generateRelativePath(appInfo, saveDir, saveName);
        ImageModel imageModel = new ImageModel();
        imageModel.setOriginalName(originName);
        imageModel.setSaveName(saveName);
        imageModel.setAbsolutePath(absolutePath);
        imageModel.setRelativePath(relativePath);
        imageModel.setUrl(generateUrl(relativePath));
        imageModel.setOwner(appInfo.getAppKey());
        imageModel.setUploadTime(System.currentTimeMillis());
        if (isAdmin) {
            imageModel.setCreator(configService.getAdminUsername());
        } else {
            imageModel.setCreator(appInfo.getAppKey());
        }
        imageModel.setSize(image.getSize());
        imageModel.setStatus(ImageStatus.NORMAL.getCode());
        fileService.saveToFile(image, absolutePath);
        return convertToDto(imageRepository.save(imageModel));
    }

    private String generateAbsolutePath(AppInfo app, String dir, String name) {
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

    private String tryCheckDir(String dir) {
        if (StringUtil.isEmpty(dir)) return null;
        if (!FileUtil.checkDirname(dir)) throw new BadRequestException("文件夹名不合法！");
        if (dir.contains("\\")) throw new BadRequestException("目前不支持上传到子文件夹！" + dir);
        String correct = FileUtil.ensureNoPrefix(FileUtil.ensureNoSuffix(dir));
        if (correct.contains("/")) throw new BadRequestException("目前不支持上传到子文件夹！" + dir);
        return correct;
    }

    @Override
    public ImageModel deleteImage(Long imageId, String operation) {
        ImageModel imageModel = findImageWithCheck(imageId);
        fileService.moveFileToTrash(configService.getSaveRootPath(), imageModel.getAbsolutePath());
        imageModel.setStatus(ImageStatus.DELETED.getCode());
        imageModel.setUpdateTime(System.currentTimeMillis());
        return imageRepository.save(imageModel);
    }

    @Override
    public void deleteImagesByAppKey(String appKey) {
        String rootPath = configService.getSaveRootPath();
        imageRepository.findAllByOwner(appKey).forEach(image -> {
            fileService.moveFileToTrash(rootPath, image.getAbsolutePath());
            image.setStatus(ImageStatus.DELETED.getCode());
            image.setUpdateTime(System.currentTimeMillis());
            imageRepository.save(image);
        });
    }

    @Override
    public void undeleteImage(Long imageId, String operator) {
        ImageModel imageModel = findImageWithCheck(imageId);
        fileService.moveFileToBack(configService.getSaveRootPath(), imageModel.getSaveName(), imageModel.getAbsolutePath());
        imageModel.setStatus(ImageStatus.NORMAL.getCode());
        imageModel.setUpdateTime(System.currentTimeMillis());
        imageRepository.save(imageModel);
    }

    @Override
    public void undeleteImagesByAppKey(String appKey) {
        String rootPath = configService.getSaveRootPath();
        imageRepository.findAllByOwner(appKey).forEach(image -> {
            fileService.moveFileToBack(rootPath, image.getSaveName(), image.getAbsolutePath());
            image.setStatus(ImageStatus.NORMAL.getCode());
            image.setUpdateTime(System.currentTimeMillis());
            imageRepository.save(image);
        });
    }

    @Override
    public void deleteImageForever(Long imageId, String operator) {
        ImageModel imageModel = findImageWithCheck(imageId);
        fileService.deleteFile(configService.getSaveRootPath(), imageModel.getSaveName(), imageModel.getAbsolutePath());
        imageRepository.delete(imageModel);
    }

    @Override
    public void deleteImagesForeverByAppKey(String appKey) {
        String rootPath = configService.getSaveRootPath();
        imageRepository.findAllByOwner(appKey).forEach(image -> {
            fileService.deleteFile(rootPath, image.getSaveName(), image.getAbsolutePath());
            imageRepository.delete(image);
        });
    }

    private ImageModel findImageWithCheck(Long imageId) {
        Optional<ImageModel> original = imageRepository.findById(imageId);
        return original.orElseThrow(() -> new NotFoundException("图片不存在", imageId));
    }

    private ImageDTO convertToDto(ImageModel imageModel) {
        ImageDTO dto = new ImageDTO();
        dto.setId(imageModel.getId());
        dto.setName(imageModel.getSaveName());
        dto.setOriginalName(imageModel.getOriginalName());
        dto.setUrl(imageModel.getUrl());
        dto.setOwnerApp(appInfoService.findByAppKey(imageModel.getOwner()).getName());
        dto.setUploadTime(imageModel.getUploadTime());
        dto.setStatus(ImageStatus.fromCode(imageModel.getStatus()));
        dto.setCreator(imageModel.getCreator());
        dto.setSize(imageModel.getSize());
        return dto;
    }
}
