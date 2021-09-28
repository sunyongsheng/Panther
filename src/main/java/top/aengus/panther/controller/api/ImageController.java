package top.aengus.panther.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.service.AppInfoService;
import top.aengus.panther.service.ImageService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
public class ImageController extends ApiV1Controller {

    private final ImageService imageService;
    private final AppInfoService appInfoService;

    @Autowired
    public ImageController(ImageService imageService, AppInfoService appInfoService) {
        this.imageService = imageService;
        this.appInfoService = appInfoService;
    }

    /**
     * App上传图片API POST /api/v1/image
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
        return response.success().msg("保存成功").data(imageService.saveImage(file, dirPath, appKey, false));
    }

    /**
     * 超级管理员上传图片API
     */
    @PostMapping("/admin/image")
    public Response<ImageDTO> adminUpload(@RequestParam(value = "app_key", required = false) String appKey,
                                          @RequestParam(value = "dir", required = false) String dirPath,
                                          @RequestParam("file") MultipartFile file) {
        Response<ImageDTO> response = new Response<>();
        return response.success().msg("保存成功").data(imageService.saveImage(file, dirPath, appKey, true));
    }

    /**
     * URL GET /api/v1/images
     */
    @GetMapping("/images")
    public Response<Page<ImageDTO>> getAllImage(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return new Response<Page<ImageDTO>>().success().data(imageService.findAll(page, pageSize));
    }

    @GetMapping("/images/{owner}")
    public Response<List<ImageDTO>> getAllImageByApp(@PathVariable(value = "owner") String appKey) {
        return new Response<List<ImageDTO>>().success().data(imageService.findAllByAppKey(appKey));
    }

    @DeleteMapping("/image/{owner}/{id}")
    public Response<Boolean> delete(@PathVariable("owner") String appKey, @PathVariable("id") Long imageId) {
        Response<Boolean> response = new Response<>();
        return response.success().data(imageService.deleteImage(imageId, appKey));
    }
}
