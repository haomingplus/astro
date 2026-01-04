package com.mdmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码请求数据传输对象
 *
 * <p>用于接收前端修改密码的请求参数</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
public class PasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度为6-50个字符")
    private String newPassword;
}
