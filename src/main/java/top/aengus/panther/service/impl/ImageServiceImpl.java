package top.aengus.panther.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Constants;
import top.aengus.panther.dao.ImageRepository;
import top.aengus.panther.enums.AppSettingKey;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.enums.ImageStatus;
import top.aengus.panther.enums.NamingStrategy;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.NotFoundException;
import top.aengus.panther.model.FileTree;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.image.RefreshResult;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.model.image.ImageModel;
import top.aengus.panther.service.*;
import top.aengus.panther.tool.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
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
    public long countInTimePeriodByUploadTime(Long startTime, Long endTime) {
        return imageRepository.countAllByUploadTimeAfterAndUploadTimeBefore(startTime, endTime);
    }

    @Override
    public Page<ImageDTO> findAll(int page, int pageSize, Sort.Direction direction, String orderBy) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, validateOrderBy(orderBy)));
        return imageRepository.findAll(pageRequest).map(this::convertToDto);
    }

    private String validateOrderBy(String orderBy) {
        if (orderBy.equals("uploadTime") || orderBy.equals("size")) return orderBy;
        return "uploadTime";
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
        if (appInfo == AppInfo.empty()) {
            throw new NotFoundException("App不存在！请先创建App", appKey);
        }
        AppStatus appStatus = AppStatus.fromCode(appInfo.getStatus());
        if (appStatus == AppStatus.LOCKED) {
            throw new BadRequestException("App已被锁定！请联系管理员");
        } else if (appStatus == AppStatus.DELETED) {
            throw new BadRequestException("App已被删除！请联系管理员");
        }

        NamingStrategy namingStrategy = NamingStrategy.valueOf(appSettingService.findAppSettingValue(appInfo, AppSettingKey.IMG_NAMING_STRATEGY));
        String saveName = generateName(namingStrategy, originName);

        String defaultSaveDir = appSettingService.findAppSettingValue(appInfo, AppSettingKey.DEFAULT_SAVE_DIR);
        String relativePath = generateRelativePath(appInfo, saveDir, saveName, defaultSaveDir);
        String absolutePath = generateAbsolutePath(relativePath);
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

    private String generateRelativePath(AppInfo app, String dir, String name, String defaultSaveDir) {
        if (app.isSuperRole() && StringUtil.isNotEmpty(dir)) {
            String correctDir =  FileUtil.ensureSuffix(FileUtil.ensurePrefix(dir));
            if (FileUtil.isAppDirIllegal(correctDir, app.getEnglishName())) {
                throw new BadRequestException("不允许上传到app目录下其他文件夹中");
            }
            return correctDir + name;
        } else {
            return FileUtil.ensureSuffix(FileUtil.ensurePrefix(defaultSaveDir)) + name;
        }
    }

    private String generateAbsolutePath(String relativePath) {
        File file = new File(configService.getSaveRootPath(), relativePath);
        String path = file.getAbsolutePath();
        if (SystemUtil.isWindows()) {
            return FileUtil.modifyPathSeparator(path);
        } else {
            return path;
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

    // 返回 /xxx/xx/x 格式
    private String tryCheckDir(String dir) {
        if (StringUtil.isEmpty(dir)) return null;
        if (FileUtil.isPathIllegal(dir)) throw new BadRequestException("请使用 / 作为路径分隔符！");
        if (FileUtil.isDirnameIllegal(dir)) throw new BadRequestException("文件夹名不合法！");
        return FileUtil.ensurePrefix(FileUtil.ensureNoSuffix(dir));
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
    public void deleteImagesWithAppAuto(String appKey) {
        String rootPath = configService.getSaveRootPath();
        imageRepository.findAllByOwnerAndStatus(appKey, ImageStatus.NORMAL.getCode()).forEach(image -> {
            fileService.moveFileToTrashWithCatch(rootPath, image.getAbsolutePath());
            image.setStatus(ImageStatus.DELETED_AUTO.getCode());
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
    public void undeleteImagesWithAppAuto(String appKey) {
        String rootPath = configService.getSaveRootPath();
        imageRepository.findAllByOwnerAndStatus(appKey, ImageStatus.DELETED_AUTO.getCode()).forEach(image -> {
            fileService.moveFileToBackWithCatch(rootPath, image.getSaveName(), image.getAbsolutePath());
            image.setStatus(ImageStatus.NORMAL.getCode());
            image.setUpdateTime(System.currentTimeMillis());
            imageRepository.save(image);
        });
    }

    @Override
    public void deleteImageForever(Long imageId, String operator, boolean deleteFile) {
        ImageModel imageModel = findImageWithCheck(imageId);
        if (deleteFile) {
            fileService.deleteFileSafely(configService.getSaveRootPath(), imageModel.getSaveName(), imageModel.getAbsolutePath());
        }
        imageRepository.delete(imageModel);
    }

    @Override
    public void deleteImagesForeverByAppKey(String appKey) {
        String rootPath = configService.getSaveRootPath();
        imageRepository.findAllByOwner(appKey).forEach(image -> {
            fileService.deleteFileSafely(rootPath, image.getSaveName(), image.getAbsolutePath());
            imageRepository.delete(image);
        });
    }

    @Override
    public Page<ImageModel> findAllByStatus(ImageStatus status, int page, int pageSize) {
        return imageRepository.findAllByStatus(status.getCode(), PageRequest.of(page, pageSize));
    }

    @Override
    public RefreshResult refreshDatabase() {
        RefreshResult result = new RefreshResult();
        List<RefreshResult.Item> invalidDb = new ArrayList<>();
        String saveRootPath = configService.getSaveRootPath();
        int currPage = 0;
        while (true) {
            Pageable pageable = PageRequest.of(currPage, Constants.PAGE_SIZE);
            Page<ImageModel> images = imageRepository.findAllByStatus(ImageStatus.NORMAL.getCode(), pageable);
            images.forEach(image -> {
                String absPath = image.getAbsolutePath();
                boolean pathIllegal = !absPath.startsWith(saveRootPath);
                boolean fileNotExists = !new File(absPath).exists();
                if (pathIllegal || fileNotExists) {
                    RefreshResult.Item item = new RefreshResult.Item();
                    item.setId(image.getId());
                    item.setName(image.getSaveName());
                    item.setAbsolutePath(image.getAbsolutePath());
                    item.setRelativePath(image.getRelativePath());
                    item.setUrl(image.getUrl());
                    item.setOwnerApp(appInfoService.findByAppKey(image.getOwner()).getName());
                    item.setOwnerAppKey(image.getOwner());
                    item.setUploadTime(image.getUploadTime());
                    item.setSize(image.getSize());
                    if (pathIllegal) {
                        item.setDesc("不在指定存储路径下");
                    } else {
                        item.setDesc("文件不存在");
                    }
                    invalidDb.add(item);
                }
            });
            if (images.getNumberOfElements() < Constants.PAGE_SIZE) {
                break;
            }
            currPage++;
        }
        result.setInvalidItems(invalidDb);
        return result;
    }

    @Override
    public RefreshResult refreshFiles() {
        RefreshResult result = new RefreshResult();
        List<RefreshResult.Item> invalidFiles = new ArrayList<>();
        String rootPath = configService.getSaveRootPath();

        FileTree rootTree = fileService.listFiles(rootPath, false);
        String rootRePathPrefix = FileUtil.FILE_SEPARATOR;

        rootTree.forEachFile(file -> handleFile(file, invalidFiles, rootRePathPrefix, null, false));

        rootTree.forEachDir(firstLevelDir -> {
            FileTree firstLevelDirTree = fileService.listFiles(firstLevelDir.getCurrFile().getAbsolutePath(), false);
            String firstRePathPrefix = rootRePathPrefix + firstLevelDir.getCurrFile().getName() + FileUtil.FILE_SEPARATOR;

            firstLevelDirTree.forEachFile(file -> handleFile(file, invalidFiles, firstRePathPrefix, null, false));

            firstLevelDirTree.forEachDir(childDir -> {
                String childDirname = childDir.getCurrFile().getName();
                FileTree childDirTree = fileService.listFiles(childDir.getCurrFile().getAbsolutePath(), false);
                String childRelativePathPrefix = firstRePathPrefix + childDirname + FileUtil.FILE_SEPARATOR;
                childDirTree.forEachFile(file -> handleFile(file, invalidFiles, childRelativePathPrefix, childDirname, true));
            });
        });
        result.setInvalidItems(invalidFiles);
        return result;
    }

    private void handleFile(File file, List<RefreshResult.Item> invalidFiles, String relativePathPrefix, String dirname, boolean setAppName) {
        String filename = file.getName();
        if (!FileUtil.isPic(filename)) return;
        String relativePath = relativePathPrefix + filename;
        if (imageRepository.findByRelativePath(relativePath) == null) {
            RefreshResult.Item item = new RefreshResult.Item();
            item.setName(filename);
            String absPath = file.getAbsolutePath();
            if (SystemUtil.isWindows()) {
                absPath = FileUtil.modifyPathSeparator(absPath);
            }
            item.setAbsolutePath(absPath);
            item.setRelativePath(relativePath);
            item.setUrl(generateUrl(relativePath));
            if (setAppName) {
                AppInfo appInfo = appInfoService.findByEnglishName(dirname);
                item.setOwnerApp(appInfo.getName());
                item.setOwnerAppKey(appInfo.getAppKey());
            } else {
                item.setOwnerApp(Constants.UNKNOWN_APP_NAME);
                item.setOwnerAppKey(Constants.UNKNOWN_APP_KEY);
            }
            item.setSize(file.length());
            try {
                item.setUploadTime(Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toMillis());
            } catch (Exception ignore) {
                item.setUploadTime(System.currentTimeMillis());
            }
            invalidFiles.add(item);
        }
    }

    @Override
    public void insertFromRefreshResult(List<RefreshResult.Item> files) {
        files.forEach(file -> {
            AppInfo app = appInfoService.findByAppKey(file.getOwnerAppKey());
            if (app != AppInfo.empty()) {
                if (FileUtil.isAppDirIllegal(file.getRelativePath(), app.getEnglishName())) {
                    throw new BadRequestException("此图片在app目录下，其所属App的英文名必须和文件名相同，请手动创建App或者移动图片");
                }
            }
            ImageModel imageModel = new ImageModel();
            imageModel.setUrl(file.getUrl());
            imageModel.setOriginalName(file.getName());
            imageModel.setSaveName(file.getName());
            imageModel.setAbsolutePath(file.getAbsolutePath());
            imageModel.setRelativePath(file.getRelativePath());
            imageModel.setOwner(file.getOwnerAppKey());
            imageModel.setUploadTime(file.getUploadTime());
            imageModel.setCreator(configService.getAdminUsername());
            imageModel.setSize(file.getSize());
            imageModel.setStatus(ImageStatus.NORMAL.getCode());
            imageRepository.save(imageModel);
        });
    }

    @Override
    public void updateImageHostUrl(String hostUrl) {
        int currPage = 0;
        while (true) {
            Pageable pageable = PageRequest.of(currPage, Constants.PAGE_SIZE);
            Page<ImageModel> images = imageRepository.findAll(pageable);
            Page<ImageModel> newImages = images.map(image -> {
                String newUrl = UrlUtil.ensureNoSuffix(hostUrl) + image.getRelativePath();
                image.setUrl(newUrl);
                return image;
            });
            imageRepository.saveAll(newImages);
            if (images.getNumberOfElements() < Constants.PAGE_SIZE) {
                break;
            }
            currPage++;
        }
    }

    @Override
    public void updateImageSavePath(String newRootPath) {
        if (FileUtil.isPathIllegal(newRootPath)) {
            throw new BadRequestException("请使用 / 作为路径分隔符");
        }
        String originalPath = configService.getSaveRootPath();
        fileService.moveFile(originalPath, newRootPath);

        int currPage = 0;
        while (true) {
            Pageable pageable = PageRequest.of(currPage, Constants.PAGE_SIZE);
            Page<ImageModel> images = imageRepository.findAll(pageable);
            Page<ImageModel> newImages = images.map(image -> {
                String newPath = FileUtil.ensureNoSuffix(newRootPath) + image.getRelativePath();
                image.setAbsolutePath(newPath);
                return image;
            });
            imageRepository.saveAll(newImages);
            if (images.getNumberOfElements() < Constants.PAGE_SIZE) {
                break;
            }
            currPage++;
        }
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
        dto.setStatus(ImageStatus.fromCodeForDTO(imageModel.getStatus()));
        dto.setCreator(imageModel.getCreator());
        dto.setSize(imageModel.getSize());
        return dto;
    }
}
