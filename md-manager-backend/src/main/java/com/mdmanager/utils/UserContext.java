package com.mdmanager.utils;

/**
 * 用户上下文工具类
 *
 * <p>使用ThreadLocal存储当前登录用户的信息，供整个请求链路使用</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
public class UserContext {

    /**
     * 存储用户ID的ThreadLocal
     */
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    /**
     * 存储用户名的ThreadLocal
     */
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    /**
     * 私有构造方法，防止实例化
     */
    private UserContext() {
    }

    /**
     * 设置当前用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置当前用户名
     *
     * @param username 用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        return USERNAME.get();
    }

    /**
     * 清除上下文数据
     *
     * <p>在请求结束后调用，防止内存泄漏</p>
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
    }
}
