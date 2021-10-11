package top.aengus.panther.model.setting;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "tb_app_setting")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AppSetting {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_id")
    private Long appId;

    @Column(name = "setting_key")
    private String key;

    @Column(name = "setting_value")
    private String value;

    @Column(name = "update_time")
    private Long updateTime;
}
