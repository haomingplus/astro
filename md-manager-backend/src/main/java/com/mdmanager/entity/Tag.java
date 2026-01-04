package com.mdmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签实体类
 *
 * <p>对应数据库表 t_tag，存储文章标签信息</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
@TableName("t_tag")  // 表名会自动加上前缀 t_
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签别名，用于URL
     */
    private String slug;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 排序序号，值越小越靠前
     */
    private Integer sortOrder;

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

    /**
     * 文章数量（非数据库字段，用于统计显示）
     */
    @TableField(exist = false)
    private Integer articleCount;
}
