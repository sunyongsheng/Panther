package top.aengus.panther.model.image;

import lombok.Data;

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

    private String owner;

    private Long uploadTime;

    private String creator;

    private Long size;

}
