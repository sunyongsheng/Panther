package top.aengus.panther.model.config;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "tb_panther_config")
@Data
public class PantherConfig {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String configKey;

    private String configValue;

    private Long updateTime;

}
