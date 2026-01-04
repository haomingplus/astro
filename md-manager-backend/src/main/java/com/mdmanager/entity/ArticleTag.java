package com.mdmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章标签关联实体类
 *
 * <p>对应数据库表 t_article_tag，存储文章与标签的多对多关联关系</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
@TableName("article_tag")  // 表名会自动加上前缀 t_
public class ArticleTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
