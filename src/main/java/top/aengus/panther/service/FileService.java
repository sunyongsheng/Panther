package top.aengus.panther.service;

import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.model.FileTree;

import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/14
 */
public interface FileService {

    void initWorkspace(String rootPath, List<String> imgDirs);

    void initAppWorkspace(String rootPath, String appName);

    void saveToFile(MultipartFile file, String absolutePath);

    /**
     * 将文件移动至回收站中
     *
     * @param rootPath Panther保存文件根目录 @see PantherConfigService.getSaveRootPath()
     * @param absolutePath 文件绝对路径
     */
    void moveFileToTrash(String rootPath, String absolutePath);

    void moveFileToTrashWithCatch(String rootPath, String absolutePath);

    /**
     * 将文件移动回原位置
     * @param filename 文件名
     * @param originalPath 原来的位置
     */
    void moveFileToBack(String rootPath, String filename, String originalPath);

    void moveFileToBackWithCatch(String rootPath, String filename, String originalPath);

    /**
     * 删除文件
     * 1. 先去回收站中寻找文件；
     * 2. 回收站中找不到则直接删除文件；
     *
     * @param filename 文件名
     * @param absolutePath 文件绝对路径
     */
    void deleteFile(String rootPath, String filename, String absolutePath);

    void deleteFileSafely(String rootPath, String filename, String absolutePath);

    void moveFile(String originalPath, String targetPath);

    /**
     * 列出所有文件
     */
    FileTree listFiles(String rootPath, boolean recursion);

}
