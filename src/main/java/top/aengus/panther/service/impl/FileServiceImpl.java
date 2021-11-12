package top.aengus.panther.service.impl;

import cn.hutool.core.collection.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Constants;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.InternalException;
import top.aengus.panther.exception.NotFoundException;
import top.aengus.panther.model.FileTree;
import top.aengus.panther.service.FileService;
import top.aengus.panther.tool.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/14
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final String DELETED = ".deleted";
    private static volatile File DELETED_FILE;

    @Override
    public void initWorkspace(String rootPath, List<String> imgDirs) {
        if (!imgDirs.containsAll(ListUtil.of(Constants.DIRNAME_APP))) {
            throw new BadRequestException("app文件夹为必选项");
        }
        File rootFile = new File(rootPath);
        if (!FileUtil.checkAndCreateDir(rootFile)) {
            throw new InternalException("创建「" + rootFile.getAbsolutePath() + "」文件夹失败，请手动创建");
        }
        File deleted = getDeletedFile(rootPath);
        if (!FileUtil.checkAndCreateDir(deleted)) {
            throw new InternalException("创建「" + deleted.getAbsolutePath() + "」文件夹失败，请手动创建");
        }
        for (String dir : imgDirs) {
            File dirFile = new File(rootPath, dir);
            if (!FileUtil.checkAndCreateDir(dirFile)) {
                throw new InternalException("创建「" + dirFile.getAbsolutePath() + "」文件夹失败，请手动创建");
            }
        }
    }

    @Override
    public void initAppWorkspace(String rootPath, String appName) {
        File appFile = new File(rootPath, Constants.DIRNAME_APP);
        File appSpecial = new File(appFile, appName);
        FileUtil.checkAndCreateDir(appSpecial);
    }

    @Override
    public String getAppWorkspaceDir(String appName) {
        return Constants.FILE_SEPARATOR + Constants.DIRNAME_APP + Constants.FILE_SEPARATOR + appName;
    }

    @Override
    public void saveToFile(MultipartFile file, String absolutePath) {
        File dest = new File(absolutePath);
        if (dest.exists()) {
            throw new BadRequestException("文件已存在，请尝试修改文件名");
        }
        FileUtil.checkAndCreateDir(dest.getParentFile());
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("保存失败", e);
            throw new InternalException("保存文件失败！");
        }
    }

    @Override
    public void moveFileToTrash(String rootPath, String absolutePath) {
        File original = new File(absolutePath);
        if (!original.exists()) {
            throw new NotFoundException("文件不存在！", absolutePath);
        }
        File deletedFile = getDeletedFile(rootPath);
        File target = new File(deletedFile, original.getName());
        if (!original.renameTo(target)) {
            throw new InternalException("删除文件失败，可能是回收站目录损坏！");
        }
    }

    @Override
    public void moveFileToBack(String rootPath, String filename, String originalPath) {
        File deletedFile = getDeletedFile(rootPath);
        File targetFile = new File(deletedFile, filename);
        if (!targetFile.exists()) {
            throw new InternalException("原文件已被删除，无法恢复文件！");
        }
        File originalFile = new File(originalPath);
        if (!targetFile.renameTo(originalFile)) {
            throw new InternalException("无法恢复文件，可能是有重名文件存在！");
        }
    }

    @Override
    public void moveFileToTrashWithCatch(String rootPath, String absolutePath) {
        try {
            moveFileToTrash(rootPath, absolutePath);
        } catch (Exception e) {
            log.error("moveFileToTrash出现了异常", e);
        }
    }

    @Override
    public void moveFileToBackWithCatch(String rootPath, String filename, String originalPath) {
        try {
            moveFileToBack(rootPath, filename, originalPath);
        } catch (Exception e) {
            log.error("moveFileToBack出现了异常", e);
        }
    }

    @Override
    public void deleteFile(String rootPath, String filename, String absolutePath) {
        File deletedFile = getDeletedFile(rootPath);
        File targetFile = new File(deletedFile, filename);
        if (targetFile.exists()) {
            if (!targetFile.delete()) {
                throw new InternalException("删除回收站文件失败");
            }
        } else {
            File fallback = new File(absolutePath);
            if (fallback.exists()) {
                if (!fallback.delete()) {
                    throw new InternalException("删除原文件失败");
                }
            } else {
                throw new NotFoundException("文件不存在！", absolutePath);
            }
        }
    }

    @Override
    public void deleteFileSafely(String rootPath, String filename, String absolutePath) {
        try {
            deleteFile(rootPath, filename, absolutePath);
        } catch (InternalException | NotFoundException exception) {
            log.error("deleteFileSafely出现异常", exception);
        }
    }

    @Override
    public void moveFile(String originalPath, String targetPath) {
        File original = new File(originalPath);
        if (!original.exists()) {
            throw new BadRequestException("原始路径不存在！");
        }
        File target = new File(targetPath);
        if (!original.renameTo(target)) {
            log.error("移动目录 {} 到 {} 失败", original, target);
        }
    }

    @Override
    public FileTree listFiles(String rootPath, boolean recursion) {
        File root = new File(rootPath);
        FileTree result = new FileTree(root);
        readPath(result, root, recursion);
        return result;
    }

    private void readPath(FileTree fileTree, File currDir, boolean recursion) {
        File[] childFiles = currDir.listFiles((dir, name) -> !name.equals(DELETED));
        if (childFiles != null) {
            for (File child : childFiles) {
                if (child.isDirectory()) {
                    FileTree directory = new FileTree(child);
                    if (recursion) readPath(directory, child, true);
                    fileTree.append(directory);
                } else {
                    fileTree.append(child);
                }
            }
        }
    }

    private File getDeletedFile(String basePath) {
        if (DELETED_FILE == null) {
            synchronized (FileServiceImpl.class) {
                if (DELETED_FILE == null) {
                    DELETED_FILE = new File(basePath, DELETED);
                }
            }
        }
        return DELETED_FILE;
    }
}
