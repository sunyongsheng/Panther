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

    /**
     * 初始化Panther工作目录：
     * 1. 创建根目录文件夹
     * 2. 创建[imgDirs]中的文件夹
     * @param rootPath 根目录
     * @param imgDirs 需要创建的文件夹
     * @throws top.aengus.panther.exception.BadRequestException [imgDirs]不包含app与common文件夹或创建文件夹失败
     */
    void initWorkspace(String rootPath, List<String> imgDirs);

    /**
     * 初始化App目录
     * @param rootPath 根目录
     * @param appName App英文名称
     */
    void initAppWorkspace(String rootPath, String appName);

    /**
     * 将文件保存到磁盘上
     * @param file 待保存文件
     * @param absolutePath 文件的绝对路径
     * @throws top.aengus.panther.exception.BadRequestException 绝对路径对应文件已存在
     * @throws top.aengus.panther.exception.InternalException 保存文件失败
     */
    void saveToFile(MultipartFile file, String absolutePath);

    /**
     * 将文件移动至回收站中
     * @param rootPath Panther保存文件根目录 @see PantherConfigService.getSaveRootPath()
     * @param absolutePath 文件绝对路径
     * @throws top.aengus.panther.exception.NotFoundException 绝对路径对应的文件不存在
     * @throws top.aengus.panther.exception.InternalException 移动文件失败
     */
    void moveFileToTrash(String rootPath, String absolutePath);

    void moveFileToTrashWithCatch(String rootPath, String absolutePath);

    /**
     * 将文件移动回原位置
     * @param filename 文件名
     * @param originalPath 原来的位置
     * @throws top.aengus.panther.exception.InternalException 回收站中的文件已被删除或originalPath对应的路径上有文件
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

    /**
     * 移动文件
     */
    void moveFile(String originalPath, String targetPath);

    /**
     * 列出所有文件
     * @param recursion 是否对子文件夹进行递归
     */
    FileTree listFiles(String rootPath, boolean recursion);

}
