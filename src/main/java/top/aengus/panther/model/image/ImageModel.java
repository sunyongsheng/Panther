package top.aengus.panther.model.image;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import top.aengus.panther.enums.ImageStatus;

import javax.persistence.*;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/19
 */
@Entity
@Table(name = "tb_image")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ImageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private String originalName;

    private String saveName;

    private String absolutePath;

    private String relativePath;

    // AppKey
    private String owner;

    private Long uploadTime;

    // AppKey Or AdminUsername
    private String creator;

    private Long size;

    private int status;

    private Long updateTime;

}
