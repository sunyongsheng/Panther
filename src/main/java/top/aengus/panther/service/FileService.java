package top.aengus.panther.service;

import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/14
 */
public interface FileService {

    void initWorkspace(String rootPath, List<String> imgDirs);

    void initAppWorkspace(String rootPath, String appName);

    /**
     * 将文件移动至回收站中
     *
     * @param rootPath Panther保存文件根目录 @see PantherConfigService.getSaveRootPath()
     * @param absolutePath 文件绝对路径
     */
    void moveFileToTrash(String rootPath, String absolutePath);

    /**
     * 删除文件
     * 1. 先去回收站中寻找文件；
     * 2. 回收站中找不到则直接删除文件；
     *
     * @param filename 文件名
     * @param absolutePath 文件绝对路径
     */
    void deleteFile(String rootPath,String filename, String absolutePath);

    List<String> getWorkspaceDirs(String rootPath);

}
