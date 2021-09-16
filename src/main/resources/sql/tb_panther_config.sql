DROP TABLE IF EXISTS `tb_panther_config`;
CREATE TABLE `tb_panther_config`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `config_key`         VARCHAR(128) NOT NULL COMMENT '键',
    `config_value`       VARCHAR(512) NOT NULL COMMENT '值',
    `update_time` BIGINT       NOT NULL DEFAULT 0 COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `config_key` (`config_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment 'Panther全局配置表';
