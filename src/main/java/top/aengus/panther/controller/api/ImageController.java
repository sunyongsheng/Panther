package top.aengus.panther.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.app.AppInfo;
import top.aengus.panther.model.image.ImageDTO;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.tool.ImageDirUtil;
import top.aengus.panther.tool.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
public class ImageController extends ApiV1Controller {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * POST /api/v1/image
     *
     * @param dirPath 保存目录，
     * @param file 图片文件
     */
    @PostMapping("/image")
    public Response<ImageDTO> upload(HttpServletRequest request,
                                     @RequestParam(value = "dir", required = false) String dirPath,
                                     @RequestParam("file") MultipartFile file) {
        Response<ImageDTO> response = new Response<>();

        AppInfo appInfo = (AppInfo) request.getAttribute(Constants.REQUEST_APP_INFO_INTERNAL);
        return response.success().msg("保存成功").data(imageService.saveImage(file, dirPath, appInfo));
    }

    /**
     * URL GET /api/v1/images
     */
    @GetMapping("/images")
    public Response<List<ImageDTO>> getAllImage(HttpServletRequest request) {
        Response<List<ImageDTO>> response = new Response<>();
        AppInfo appInfo = (AppInfo) request.getAttribute(Constants.REQUEST_APP_INFO_INTERNAL);
        return response.data(imageService.findAllByAppId(appInfo.getAppId()));
    }

    @DeleteMapping("/image/{id}")
    public Response<Boolean> delete(HttpServletRequest request, @PathVariable("id") Long imageId) {
        Response<Boolean> response = new Response<>();
        AppInfo appInfo = (AppInfo) request.getAttribute(Constants.REQUEST_APP_INFO_INTERNAL);
        return response.data(imageService.deleteImage(imageId, appInfo.getAppId()));
    }
}
