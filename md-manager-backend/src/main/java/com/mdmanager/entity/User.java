package com.mdmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * <p>对应数据库表 t_user，存储系统管理员用户信息</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
@TableName("t_user")  // 表名会自动加上前缀 t_
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名，登录账号
     */
    private String username;

    /**
     * 密码，加密存储
     */
    private String password;

    /**
     * 昵称，显示名称
     */
    private String nickname;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

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
