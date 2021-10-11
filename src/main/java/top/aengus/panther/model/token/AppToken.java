package top.aengus.panther.model.token;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/26
 */
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "tb_app_token")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AppToken {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_key")
    private String appKey;

    @Column(name = "token")
    private String token;

    @Column(name = "stage")
    private String stage;

    @Column(name = "generate_time")
    private Long generateTime;

    @Column(name = "update_time")
    private Long updateTime;
}
