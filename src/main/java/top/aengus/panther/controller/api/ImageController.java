package top.aengus.panther.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.CommonParam;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.model.image.ImageModel;
import top.aengus.panther.model.image.RefreshResult;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.service.PantherConfigService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Slf4j
@RestController
public class ImageController extends ApiV1Controller {

    private final ImageService imageService;
    private final PantherConfigService configService;

    @Autowired
    public ImageController(ImageService imageService, PantherConfigService configService) {
        this.imageService = imageService;
        this.configService = configService;
    }

    /**
     * App上传图片API POST /api/v1/image
     * 此接口由 {@link top.aengus.panther.security.ImageUploadFilter} 进行拦截
     *
     * @param dirPath 保存目录，
     * @param file 图片文件
     */
    @PostMapping("/image")
    public Response<ImageDTO> upload(HttpServletRequest request,
                                     @RequestParam(value = "dir", required = false) String dirPath,
                                     @RequestParam("file") MultipartFile file) {
        Response<ImageDTO> response = new Response<>();
        String appKey = request.getAttribute(Constants.Header.INTERNAL_APP_KEY).toString();
        return response.success().msg("上传成功").data(imageService.saveImage(file, dirPath, appKey, false));
    }

    /**
     * 超级管理员上传图片API
     */
    @PostMapping("/admin/image")
    public Response<ImageDTO> adminUpload(@RequestParam(value = "app_key") String appKey,
                                          @RequestParam(value = "dir", required = false) String dirPath,
                                          @RequestParam("file") MultipartFile file) {
        Response<ImageDTO> response = new Response<>();
        return response.success().msg("上传成功").data(imageService.saveImage(file, dirPath, appKey, true));
    }

    /**
     * URL GET /api/v1/images
     */
    @GetMapping("/images")
    public Response<Page<ImageDTO>> getAllImage(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "page_size", defaultValue = "20") int pageSize,
                                                @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction,
                                                @RequestParam(value = "order_by", defaultValue = "uploadTime") String orderBy) {
        return new Response<Page<ImageDTO>>().success().data(imageService.findAll(page, pageSize, direction, orderBy));
    }

    @GetMapping("/images/{owner}")
    public Response<List<ImageDTO>> getAllImageByApp(@PathVariable(value = "owner") String appKey) {
        return new Response<List<ImageDTO>>().success().data(imageService.findAllByAppKey(appKey));
    }

    @PostMapping("/image/delete/{id}")
    public Response<Void> delete(@PathVariable("id") Long imageId) {
        Response<Void> response = new Response<>();
        ImageModel model = imageService.deleteImage(imageId, configService.getAdminUsername());
        return response.success().msg(model.getSaveName() + " 删除成功");
    }

    @PostMapping("/image/undelete/{id}")
    public Response<Void> undelete(@PathVariable("id") Long imageId) {
        imageService.undeleteImage(imageId, configService.getAdminUsername());
        return new Response<Void>().success().msg("恢复成功");
    }

    @DeleteMapping("/admin/image")
    public Response<Void> deleteForever(@RequestParam("id") Long imageId,
                                        @RequestParam("delete_file") boolean deleteFile) {
        imageService.deleteImageForever(imageId, configService.getAdminUsername(), deleteFile);
        return new Response<Void>().success().msg("删除成功");
    }

    @GetMapping("/image/refresh/db")
    public Response<RefreshResult> refreshImageDb() {
        return new Response<RefreshResult>().success().data(imageService.refreshDatabase());
    }

    @GetMapping("/image/refresh/file")
    public Response<RefreshResult> refreshImageFile() {
        return new Response<RefreshResult>().success().data(imageService.refreshFiles());
    }

    @PostMapping("/admin/file/delete")
    public Response<Void> deleteFileForever(@RequestBody @Validated CommonParam<String> param) {
        Response<Void> response = new Response<>();
        if (new File(param.getKey()).delete()) {
            return response.success().msg("删除成功");
        } else {
            return response.unknownError().msg("删除失败");
        }
    }

    @PostMapping("/admin/file/save")
    public Response<Void> saveInvalidFile(@RequestBody @Validated CommonParam<RefreshResult.Item> param) {
        Response<Void> response = new Response<>();
        imageService.insertFromRefreshResult(param.getValues());
        return response.success().msg("保存成功");
    }
}
