DROP TABLE IF EXISTS `tb_app_token`;
CREATE TABLE `tb_app_token`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `app_key`       VARCHAR(64)  NOT NULL COMMENT 'AppKey',
    `token`         VARCHAR(128) NOT NULL DEFAULT '' COMMENT 'Token值',
    `stage`         VARCHAR(30)  NOT NULL DEFAULT '' COMMENT '场景',
    `generate_time` BIGINT       NOT NULL DEFAULT 0 COMMENT '生成Token的时机',
    `update_time`   BIGINT       NOT NULL DEFAULT 0 COMMENT '更新Token数据的时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`token`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT 'App Token表';