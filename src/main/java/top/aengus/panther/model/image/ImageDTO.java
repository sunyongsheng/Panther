package top.aengus.panther.model.image;

import lombok.Data;
import top.aengus.panther.enums.ImageStatus;

/**
 * @author sunyongsheng (sunyongsheng@bytedance.com)
 * <p>
 * date 2021/4/13
 */
@Data
public class ImageDTO {

    private Long id;

    // 图片文件名
    private String name;

    // 图片的URL
    private String url;

    private String ownerApp;

    private ImageStatus status;

    private Long uploadTime;

    private String creator;

    private Long size;

}
