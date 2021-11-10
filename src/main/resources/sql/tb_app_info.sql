DROP TABLE IF EXISTS `tb_app_info`;
CREATE TABLE `tb_app_info`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `app_key`     VARCHAR(64)  NOT NULL UNIQUE COMMENT 'AppKey',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT 'App角色',
    `app_name`    VARCHAR(20)  NOT NULL DEFAULT '' COMMENT 'App名称',
    `app_name_en` VARCHAR(40)  NOT NULL DEFAULT '' COMMENT 'App英文名',
    `avatar_url`  VARCHAR(200) NOT NULL DEFAULT '' COMMENT 'App图标',
    `create_time` BIGINT       NOT NULL DEFAULT 0 COMMENT '创建时间',
    `email`       VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '邮箱',
    `owner`       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT 'App拥有者',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT 'App状态',
    `update_time` BIGINT       NOT NULL DEFAULT 0 COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment 'App表';
