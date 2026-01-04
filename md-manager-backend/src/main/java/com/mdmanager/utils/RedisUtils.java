package com.mdmanager.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 *
 * <p>封装Redis常用操作方法</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户会话缓存前缀
     */
    private static final String USER_SESSION_PREFIX = "md:user:session:";

    /**
     * 用户会话过期时间（秒）：24小时
     */
    private static final long USER_SESSION_EXPIRE = 24 * 60 * 60;

    // ==================== 通用操作 ====================

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis set error: {}", e.getMessage());
        }
    }

    /**
     * 设置缓存（带过期时间）
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis set error: {}", e.getMessage());
        }
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return 是否成功
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis delete error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true存在，false不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis hasKey error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间（秒）
     * @return 是否成功
     */
    public boolean expire(String key, long timeout) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("Redis expire error: {}", e.getMessage());
            return false;
        }
    }

    // ==================== 用户会话操作 ====================

    /**
     * 保存用户会话
     *
     * @param token    Token
     * @param userId   用户ID
     * @param userInfo 用户信息
     */
    public void saveUserSession(String token, Long userId, Object userInfo) {
        String key = USER_SESSION_PREFIX + token;
        set(key, userInfo, USER_SESSION_EXPIRE);
        log.debug("保存用户会话: userId={}, token={}", userId, token);
    }

    /**
     * 获取用户会话
     *
     * @param token Token
     * @return 用户信息
     */
    public Object getUserSession(String token) {
        String key = USER_SESSION_PREFIX + token;
        return get(key);
    }

    /**
     * 删除用户会话
     *
     * @param token Token
     */
    public void removeUserSession(String token) {
        String key = USER_SESSION_PREFIX + token;
        delete(key);
        log.debug("删除用户会话: token={}", token);
    }

    /**
     * 刷新用户会话过期时间
     *
     * @param token Token
     */
    public void refreshUserSession(String token) {
        String key = USER_SESSION_PREFIX + token;
        expire(key, USER_SESSION_EXPIRE);
    }

    /**
     * 检查用户会话是否存在
     *
     * @param token Token
     * @return true存在，false不存在
     */
    public boolean hasUserSession(String token) {
        String key = USER_SESSION_PREFIX + token;
        return hasKey(key);
    }
}
