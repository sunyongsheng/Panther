DROP TABLE IF EXISTS `tb_app_info`;
CREATE TABLE `tb_app_info`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `app_key`     VARCHAR(64)  NOT NULL UNIQUE COMMENT 'AppKey',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT 'App角色',
    `name_zh`     VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '中文名',
    `name_en`     VARCHAR(40)  NOT NULL DEFAULT '' COMMENT '英文名',
    `avatar_url`  VARCHAR(200) NOT NULL DEFAULT '' COMMENT 'App图标',
    `create_time` BIGINT       NOT NULL DEFAULT 0 COMMENT '创建时间',
    `phone`       VARCHAR(15)  NOT NULL DEFAULT '' COMMENT '联系电话',
    `email`       VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '邮箱',
    `owner`       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT 'App拥有者',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT 'App状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment 'App表';
