# Panther配置表
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

# App信息表
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

# App设置表
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

# App Token表
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

# 上传图片表
DROP TABLE IF EXISTS `tb_image`;
CREATE TABLE `tb_image`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `url`           TEXT         NOT NULL COMMENT '图片地址',
    `original_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '原始名称',
    `save_name`     VARCHAR(60)  NOT NULL DEFAULT '' COMMENT '保存名称',
    `absolute_path` TEXT         NOT NULL COMMENT '绝对路径',
    `relative_path` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '相对保存根目录路径',
    `owner`         VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '所属App Key',
    `upload_time`   BIGINT       NOT NULL DEFAULT 0 COMMENT '上传时间',
    `creator`       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '上传者，可能为管理员用户名或者AppKey',
    `size`          BIGINT       NOT NULL DEFAULT 0 COMMENT '图片大小',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '图片状态',
    `update_time`   BIGINT       NOT NULL DEFAULT 0 COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment '上传图片表';
