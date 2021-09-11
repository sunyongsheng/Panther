package top.aengus.panther.service;

import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/14
 */
public interface FileService {

    void initWorkspace(String basePath, List<String> imgDirs);

    List<String> getWorkspaceDirs(String basePath);

}
