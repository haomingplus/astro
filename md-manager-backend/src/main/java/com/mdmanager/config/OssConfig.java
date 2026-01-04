package com.mdmanager.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置类
 *
 * <p>从配置文件读取OSS相关配置，并创建OSS客户端Bean</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {

    /**
     * OSS端点
     */
    private String endpoint;

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * Bucket名称
     */
    private String bucketName;

    /**
     * 图片存储目录前缀
     */
    private String imageDir;

    /**
     * 创建OSS客户端Bean
     *
     * @return OSS客户端实例
     */
    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 获取图片访问的基础URL
     *
     * @return 图片访问URL前缀
     */
    public String getImageBaseUrl() {
        return "https://" + bucketName + "." + endpoint + "/" + imageDir;
    }
}
