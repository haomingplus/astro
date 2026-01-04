package com.mdmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置类
 *
 * <p>从配置文件读取JWT相关配置</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * 密钥
     */
    private String secret;

    /**
     * 过期时间（毫秒）
     */
    private Long expiration;

    /**
     * Token前缀
     */
    private String tokenPrefix;

    /**
     * 请求头名称
     */
    private String header;
}
