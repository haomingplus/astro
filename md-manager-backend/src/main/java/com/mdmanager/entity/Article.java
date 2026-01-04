package com.mdmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章实体类
 *
 * <p>对应数据库表 t_article，存储Markdown文章信息</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
@TableName("t_article")  // 表名会自动加上前缀 t_
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容，Markdown格式
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * MD文件名，使用UUID命名
     */
    private String fileName;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 作者
     */
    private String author;

    /**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDeleted;
}
