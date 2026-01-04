package com.mdmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户请求数据传输对象
 *
 * <p>用于接收前端新增/编辑用户的请求参数</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度为3-50个字符")
    private String username;

    /**
     * 密码（新增时必填，编辑时可选）
     */
    @Size(min = 6, max = 50, message = "密码长度为6-50个字符")
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
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
}
