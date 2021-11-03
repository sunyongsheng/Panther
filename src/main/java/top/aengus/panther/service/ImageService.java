package top.aengus.panther.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.enums.ImageStatus;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.model.image.ImageModel;
import top.aengus.panther.model.image.RefreshResult;

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

    ImageDTO saveImage(MultipartFile image, String dir, String appKey, boolean isAdmin);

    ImageModel deleteImage(Long imageId, String operator);

    /**
     * 删除App下所有状态为{@link ImageStatus#NORMAL}的图片，将其状态变为{@link ImageStatus#DELETED_AUTO}
     */
    void deleteImagesWithAppAuto(String appKey);

    void undeleteImage(Long imageId, String operator);

    /**
     * 恢复App下所有状态为{@link ImageStatus#DELETED_AUTO}的图片
     */
    void undeleteImagesWithAppAuto(String appKey);

    void deleteImageForever(Long imageId, String operator, boolean deleteFile);

    void deleteImagesForeverByAppKey(String appKey);

    Page<ImageModel> findAllByStatus(ImageStatus status, int page, int pageSize);

    RefreshResult refreshDatabase();
}
