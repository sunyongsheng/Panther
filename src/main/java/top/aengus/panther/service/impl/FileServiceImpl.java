package top.aengus.panther.service.impl;

import cn.hutool.core.collection.ListUtil;
import org.springframework.stereotype.Service;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.service.FileService;

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
        for (String dir : imgDirs) {
            File dirFile = new File(basePath, dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            } else if (!dirFile.isDirectory()) {
                throw new BadRequestException("存在 " + dir + " 同名文件，请将其删除并重新安装");
            }
        }
    }

    @Override
    public List<String> getWorkspaceDirs(String basePath) {
        return null;
    }
}
