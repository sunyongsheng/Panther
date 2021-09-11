DROP TABLE IF EXISTS `tb_image`;
CREATE TABLE `tb_image`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `url`           TEXT         NOT NULL COMMENT '图片地址',
    `original_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '原始名称',
    `save_name`     VARCHAR(60)  NOT NULL DEFAULT '' COMMENT '保存名称',
    `absolute_path` VARCHAR(120) NOT NULL DEFAULT '' COMMENT '绝对路径',
    `relative_path` VARCHAR(80)  NOT NULL DEFAULT '' COMMENT '相对服务系统路径',
    `upload_time`   BIGINT       NOT NULL DEFAULT 0 COMMENT '上传时间',
    `creator`       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '上传者',
    `size`          BIGINT       NOT NULL DEFAULT 0 COMMENT '图片大小',
    `status`        TINYINT               DEFAULT 0 COMMENT '图片状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment '上传图片表';
