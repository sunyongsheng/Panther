package top.aengus.panther.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.model.image.ImageModel;

import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/20
 */
public interface ImageService {

    long countAll();

    long countByAppKey(String appKey);

    Page<ImageDTO> findAll(int page, int pageSize);

    List<ImageDTO> findAllByAppKey(String appKey);

    ImageModel findImageByName(String filename);

    ImageDTO saveImage(MultipartFile image, String dir, String appKey, boolean isAdmin);

    boolean deleteImage(Long imageId, String operator);

    boolean deleteImageForever(Long imageId, String operator);

}
