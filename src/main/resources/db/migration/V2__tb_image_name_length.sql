ALTER TABLE `tb_image` MODIFY original_name VARCHAR(200) NOT NULL DEFAULT '' COMMENT '原始名称';
ALTER TABLE `tb_image` MODIFY save_name VARCHAR(256)  NOT NULL DEFAULT '' COMMENT '保存名称';