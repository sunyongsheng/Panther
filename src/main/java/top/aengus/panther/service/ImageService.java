package top.aengus.panther.service;

import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.model.image.ImageModel;

import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/20
 */
public interface ImageService {

    List<ImageDTO> findAllByAppKey(String appKey);

    ImageModel findImageByName(String filename);

    ImageDTO saveImage(MultipartFile image, String dir, AppInfo appInfo);

    boolean deleteImage(Long imageId, String operator);

}
