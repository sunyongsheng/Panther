DROP TABLE IF EXISTS `tb_app_token`;
CREATE TABLE `tb_app_token`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `app_id`      BIGINT       NOT NULL COMMENT 'app_info表的id字段',
    `token`       VARCHAR(128) NOT NULL DEFAULT '',
    `stage`       VARCHAR(30)  NOT NULL DEFAULT '',
    `create_time` BIGINT       NOT NULL DEFAULT 0,
    `update_time` BIGINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`token`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT 'App Token表';