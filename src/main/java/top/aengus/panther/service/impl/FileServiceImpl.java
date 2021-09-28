package top.aengus.panther.service.impl;

import cn.hutool.core.collection.ListUtil;
import org.springframework.stereotype.Service;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.InternalException;
import top.aengus.panther.service.FileService;
import top.aengus.panther.tool.FileUtil;

import java.io.File;
import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/14
 */
@Service
public class FileServiceImpl implements FileService {

    public static final String NAME_APP = "app";
    public static final String NAME_COMMON = "common";
    public static final String NAME_POST = "post";
    public static final String NAME_TRAVEL = "travel";
    public static final String NAME_SCREENSHOTS = "screenshots";

    @Override
    public void initWorkspace(String basePath, List<String> imgDirs) {
        if (!imgDirs.containsAll(ListUtil.of(NAME_APP, NAME_COMMON))) {
            throw new BadRequestException("app与common文件夹为必选项");
        }
        File rootFile = new File(basePath);
        if (!FileUtil.checkAndCreateDir(rootFile)) {
            throw new BadRequestException("创建「" + rootFile.getAbsolutePath() + "」文件夹失败，请手动创建");
        }
        for (String dir : imgDirs) {
            File dirFile = new File(basePath, dir);
            if (!FileUtil.checkAndCreateDir(dirFile)) {
                throw new BadRequestException("创建「" + dirFile.getAbsolutePath() + "」文件夹失败，请手动创建");
            }
        }
    }

    @Override
    public void initAppWorkspace(String rootPath, String appName) {
        File appFile = new File(rootPath, NAME_APP);
        File appSpecial = new File(appFile, appName);
        FileUtil.checkAndCreateDir(appSpecial);
    }

    @Override
    public List<String> getWorkspaceDirs(String basePath) {
        return null;
    }
}
