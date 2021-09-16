DROP TABLE IF EXISTS `tb_app_setting`;
CREATE TABLE `tb_app_setting`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `app_id`      BIGINT      NOT NULL COMMENT 'app_info表的id字段',
    `setting_key`         VARCHAR(30) NOT NULL DEFAULT '',
    `setting_value`       VARCHAR(30) NOT NULL DEFAULT '',
    `update_time` BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT 'App配置表';