package top.aengus.panther.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import top.aengus.panther.enums.AppRole;

import javax.persistence.*;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "tb_app_info")
@Data
public class AppInfo {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "role", columnDefinition = "TINYINT NOT NULL DEFAULT 0")
    private Integer role;

    @Column(name = "name_zh")
    private String name;

    @Column(name = "name_en")
    private String englishName;

    @Column(name = "create_time", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Long createTime;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "config_id")
    private Long configId;

    @Column(name = "status", columnDefinition = "TINYINT NOT NULL DEFAULT 0")
    private Integer status;

    public boolean isSuperRole() {
        return AppRole.SUPER.getCode().equals(role);
    }

}
