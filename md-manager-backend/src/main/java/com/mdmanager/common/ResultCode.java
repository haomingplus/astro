package com.mdmanager.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * <p>定义系统中使用的所有状态码和对应的消息</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * 未授权（未登录）
     */
    UNAUTHORIZED(401, "未登录或登录已过期，请重新登录"),

    /**
     * 禁止访问（无权限）
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "请求的资源不存在"),

    /**
     * 服务器内部错误
     */
    ERROR(500, "服务器内部错误"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(1001, "用户名或密码错误"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1002, "用户已被禁用"),

    /**
     * Token无效
     */
    TOKEN_INVALID(1003, "Token无效或已过期"),

    /**
     * 参数校验失败
     */
    VALIDATE_ERROR(1004, "参数校验失败"),

    /**
     * 文件上传失败
     */
    UPLOAD_ERROR(2001, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_ERROR(2002, "文件类型不支持"),

    /**
     * 文件大小超限
     */
    FILE_SIZE_ERROR(2003, "文件大小超过限制"),

    /**
     * 文章不存在
     */
    ARTICLE_NOT_FOUND(3001, "文章不存在"),

    /**
     * 标签不存在
     */
    TAG_NOT_FOUND(3002, "标签不存在"),

    /**
     * 标签名称重复
     */
    TAG_NAME_EXISTS(3003, "标签名称已存在");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态消息
     */
    private final String message;

    /**
     * 构造方法
     *
     * @param code    状态码
     * @param message 状态消息
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
