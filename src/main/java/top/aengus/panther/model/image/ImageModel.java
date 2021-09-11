package top.aengus.panther.model.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private String originalName;

    private String saveName;

    private String absolutePath;

    private String relativePath;

    private Long uploadTime;

    private String creator;

    private Long size;

    private int status;

    public ImageModel(String originalName, String saveName, String absolutePath, int status) {
        this.originalName = originalName;
        this.saveName = saveName;
        this.absolutePath = absolutePath;
        this.status = status;
    }

    public void delete() {
        this.status = ImageStatus.DELETED.getCode();
    }

    public boolean isDeleted() {
        return status == ImageStatus.DELETED.getCode();
    }

}
