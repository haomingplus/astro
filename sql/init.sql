-- ============================================
-- Markdown 文件管理系统数据库初始化脚本
-- 数据库：MySQL 8.0
-- 创建日期：2024
-- 说明：表名前缀使用 t_
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS md_manager
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE md_manager;

-- ============================================
-- 1. 用户表
-- 说明：存储系统管理员用户信息
-- ============================================
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名，登录账号',
    `password` VARCHAR(255) NOT NULL COMMENT '密码，加密存储',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称，显示名称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 标签表
-- 说明：存储文章标签信息
-- ============================================
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID，主键自增',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `slug` VARCHAR(100) DEFAULT NULL COMMENT '标签别名，用于URL',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '标签描述',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号，值越小越靠前',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ============================================
-- 3. 文章表
-- 说明：存储Markdown文章信息
-- ============================================
DROP TABLE IF EXISTS `t_article`;
CREATE TABLE `t_article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID，主键自增',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `content` LONGTEXT NOT NULL COMMENT '文章内容，Markdown格式',
    `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    `file_name` VARCHAR(100) NOT NULL COMMENT 'MD文件名，使用UUID命名',
    `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件存储路径',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-草稿，1-已发布',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `create_user_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_file_name` (`file_name`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_publish_time` (`publish_time`),
    KEY `idx_is_top` (`is_top`),
    FULLTEXT KEY `ft_title_content` (`title`, `content`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- ============================================
-- 4. 文章标签关联表
-- 说明：文章与标签的多对多关联
-- ============================================
DROP TABLE IF EXISTS `t_article_tag`;
CREATE TABLE `t_article_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键自增',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ============================================
-- 5. 操作日志表
-- 说明：记录用户操作日志
-- ============================================
DROP TABLE IF EXISTS `t_operation_log`;
CREATE TABLE `t_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键自增',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    `operation` VARCHAR(100) DEFAULT NULL COMMENT '操作类型',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    `result` TINYINT DEFAULT NULL COMMENT '操作结果：0-失败，1-成功',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `time_cost` BIGINT DEFAULT NULL COMMENT '耗时（毫秒）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 初始数据插入
-- ============================================

-- 插入默认管理员用户
-- 默认密码：admin123（使用BCrypt加密后的密码）
INSERT INTO `t_user` (`username`, `password`, `nickname`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKqILs9GwKpFhRVPtUMDXQcRQbZO', '系统管理员', 1);

-- 插入默认标签
INSERT INTO `t_tag` (`name`, `slug`, `description`, `sort_order`) VALUES
('Vue', 'vue', 'Vue.js相关文章', 1),
('React', 'react', 'React相关文章', 2),
('JavaScript', 'javascript', 'JavaScript相关文章', 3),
('TypeScript', 'typescript', 'TypeScript相关文章', 4),
('CSS', 'css', 'CSS样式相关文章', 5),
('Node.js', 'nodejs', 'Node.js相关文章', 6),
('后端', 'backend', '后端开发相关文章', 7),
('数据库', 'database', '数据库相关文章', 8),
('工具', 'tools', '开发工具相关文章', 9),
('其他', 'others', '其他分类文章', 10);

-- ============================================
-- 视图创建（可选）
-- ============================================

-- 文章列表视图，包含标签信息
DROP VIEW IF EXISTS `v_article_list`;
CREATE VIEW `v_article_list` AS
SELECT
    a.id,
    a.title,
    a.summary,
    a.file_name,
    a.cover_image,
    a.author,
    a.status,
    a.view_count,
    a.is_top,
    a.publish_time,
    a.create_time,
    a.update_time,
    GROUP_CONCAT(t.name SEPARATOR ',') AS tag_names,
    GROUP_CONCAT(t.id SEPARATOR ',') AS tag_ids
FROM `t_article` a
LEFT JOIN `t_article_tag` at ON a.id = at.article_id
LEFT JOIN `t_tag` t ON at.tag_id = t.id AND t.is_deleted = 0
WHERE a.is_deleted = 0
GROUP BY a.id;

-- ============================================
-- 完成提示
-- ============================================
SELECT '数据库初始化完成！' AS message;
SELECT '默认管理员账号：admin，密码：admin123' AS tips;
