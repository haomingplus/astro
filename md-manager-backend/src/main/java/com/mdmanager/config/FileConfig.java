package com.mdmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储配置类
 *
 * <p>从配置文件读取文件存储相关配置</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileConfig {

    /**
     * MD文件上传路径
     */
    private String uploadPath;

    /**
     * 允许的文件类型
     */
    private String allowedTypes;
}
