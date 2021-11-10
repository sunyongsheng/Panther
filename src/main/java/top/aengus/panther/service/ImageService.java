package top.aengus.panther.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.enums.ImageStatus;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.model.image.ImageModel;
import top.aengus.panther.model.image.RefreshResult;
import top.aengus.panther.model.setting.AppSetting;

import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/20
 */
public interface ImageService {

    /**
     * 返回图片总数量
     * @return 图片总数量
     */
    long countAll();

    /**
     * 返回{@link ImageModel#getOwner()} == appKey的App上传的图片总数
     * @param appKey 指定App的appKey
     * @return 此App上传图片的总数量
     */
    long countByAppKey(String appKey);

    /**
     * 返回某个时间段上传的图片数量
     * @param startTime 开始时间miles
     * @param endTime 结束时间miles
     * @return 此时间段上传图片的数量
     */
    long countInTimePeriodByUploadTime(Long startTime, Long endTime);

    /**
     * 查询第[page]页的所有图片，以[orderBy]为准按[direction]顺序进行排列
     * @param page 第几页，从0开始
     * @param pageSize 查询的数量
     * @param direction 排序规则
     * @param orderBy 根据哪一个字段进行排序
     * @return 符合顺序的所有图片
     */
    Page<ImageDTO> findAll(int page, int pageSize, Sort.Direction direction, String orderBy);

    /**
     * 查询所有{@link ImageModel#getOwner()} == [appKey]的ImageDTO
     *
     * @param appKey 图片所属App的appKey
     * @return 此App上传的所有ImageDTO
     */
    List<ImageDTO> findAllByAppKey(String appKey);

    /**
     * 分页查询所有状态为[status]的图片
     * @param status 图片状态
     * @param page 第几页，从0开始
     * @param pageSize 查询的数量
     * @return 分页结果
     */
    Page<ImageModel> findAllByStatus(ImageStatus status, int page, int pageSize);

    /**
     * 将图片保存到指定目录[dir]下或者App专属目录下
     * <p>1. 若{@link AppInfo#getRole()} == {@link top.aengus.panther.enums.AppRole#SUPER}且[dir]不空，则将其保存到目录[dir]中</p>
     * <p>2. 若{@link AppInfo#getRole()} == {@link top.aengus.panther.enums.AppRole#SUPER}且[dir]为空，则将其保存到App目录中</p>
     * <p>3. 若{@link AppInfo#getRole()} == {@link top.aengus.panther.enums.AppRole#NORMAL}，则忽略[dir]，只将其保存到App目录中</p>
     *
     * <p>
     * 保存图片的文件名将根据{@link AppSetting#getKey()} == {@link top.aengus.panther.enums.AppSettingKey#IMG_NAMING_STRATEGY}
     * 所对应的{@link top.aengus.panther.enums.NamingStrategy}进行生成
     * </p>
     *
     * @param image 要保存的图片
     * @param dir 要保存的目录名，可空，只能为一级目录，比如 common/post
     * @param appKey 图片所属的App的appKey，不可空
     * @param isAdmin 是否是管理员上传，若为{@code true}，则ImageModel#creator为管理员用户名，否则为appKey
     * @return 保存完成的ImageDTO
     * @throws top.aengus.panther.exception.NotFoundException 找不到appKey所对应的App
     * @throws top.aengus.panther.exception.BadRequestException image为空或非图片文件；App已被锁定或被删除；目录名不合法
     * @see FileService#saveToFile(MultipartFile, String)
     */
    ImageDTO saveImage(MultipartFile image, String dir, String appKey, boolean isAdmin);

    /**
     * 逻辑删除图片
     * @param imageId 图片的Id
     * @param operator 操作者
     * @return 删除的图片
     * @throws top.aengus.panther.exception.NotFoundException imageId对应的图片不存在
     * @see FileService#moveFileToTrash(String, String)
     */
    ImageModel deleteImage(Long imageId, String operator);

    /**
     * 删除App下所有状态为{@link ImageStatus#NORMAL}的图片，将其状态变为{@link ImageStatus#DELETED_AUTO}
     * @param appKey 图片所属App的appKey
     */
    void deleteImagesWithAppAuto(String appKey);

    /**
     * 逻辑恢复图片
     * @param imageId 图片的Id
     * @param operator 操作者
     * @throws top.aengus.panther.exception.NotFoundException imageId对应的图片不存在
     * @see FileService#moveFileToBack(String, String, String)
     */
    void undeleteImage(Long imageId, String operator);

    /**
     * 恢复App下所有状态为{@link ImageStatus#DELETED_AUTO}的图片
     * @param appKey 图片所属App的appKey
     */
    void undeleteImagesWithAppAuto(String appKey);

    /**
     * 永久删除图片记录
     * @param imageId 图片的id
     * @param operator 操作者
     * @param deleteFile 是否删除图片文件
     * @throws top.aengus.panther.exception.NotFoundException imageId对应的图片不存在
     */
    void deleteImageForever(Long imageId, String operator, boolean deleteFile);

    /**
     * 永久删除App下的所有图片记录及文件
     * @param appKey 图片所属App的appKey
     */
    void deleteImagesForeverByAppKey(String appKey);

    /**
     * 扫描数据库中所有图片，返回所有不在存储根目录的图片以及绝对路径不正确的图片
     * @return 不正确的图片记录结果，可能是不在存储根目录下或者绝对路径不正确
     */
    RefreshResult refreshDatabase();

    /**
     * 扫描存储根目录下的所有文件，最多扫描二级目录，返回所有没有在数据库中存在记录的图片信息
     * @return 没有在数据库中存在记录的图片信息
     */
    RefreshResult refreshFiles();

    /**
     * 将{@link ImageService#refreshFiles()}返回的结果插入到数据库中
     * @param files {@link ImageService#refreshFiles()}返回的结果
     */
    void insertFromRefreshResult(List<RefreshResult.Item> files);

    /**
     * 更新图片的域名地址
     * @param hostUrl 新的域名地址
     */
    void updateImageHostUrl(String hostUrl);

    /**
     * 更新图片的绝对路径并移动根目录
     * @param rootPath 新的存储目录的绝对路径
     */
    void updateImageSavePath(String rootPath);
}
