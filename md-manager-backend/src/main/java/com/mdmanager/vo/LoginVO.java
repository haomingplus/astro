package com.mdmanager.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应视图对象
 *
 * <p>用于返回登录成功后的Token和用户信息</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserVO userInfo;
}
