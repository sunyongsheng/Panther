package top.aengus.panther.model.app;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import top.aengus.panther.core.Constants;
import top.aengus.panther.enums.AppRole;

import javax.persistence.*;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "tb_app_info")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AppInfo {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_key")
    private String appKey;

    @Column(name = "role", columnDefinition = "TINYINT NOT NULL DEFAULT 0")
    private Integer role;

    @Column(name = "app_name")
    private String name;

    @Column(name = "app_name_en")
    private String englishName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "email")
    private String email;

    @Column(name = "owner")
    private String owner;

    @Column(name = "status", columnDefinition = "TINYINT NOT NULL DEFAULT 0")
    private Integer status;

    @Column(name = "update_time")
    private Long updateTime;


    @Transient
    private static volatile AppInfo EMPTY;

    public static AppInfo empty() {
        if (EMPTY == null) {
            synchronized (AppInfo.class) {
                if (EMPTY == null) {
                    EMPTY = new AppInfo();
                    EMPTY.setId(-1L);
                    EMPTY.setAppKey(Constants.UNKNOWN_APP_KEY);
                    EMPTY.setName(Constants.UNKNOWN_APP_NAME);
                    EMPTY.setEnglishName(Constants.UNKNOWN_APP_KEY);
                    EMPTY.setRole(AppRole.NORMAL.getCode());
                }
            }
        }
        return EMPTY;
    }

}
