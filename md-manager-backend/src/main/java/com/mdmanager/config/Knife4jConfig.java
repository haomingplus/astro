package com.mdmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API文档配置类
 *
 * <p>配置OpenAPI文档信息</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置 OpenAPI
     *
     * @return OpenAPI实例
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // 文档信息
                .info(new Info()
                        .title("Markdown文件管理系统 API文档")
                        .description("Markdown文件管理系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MD Manager")
                                .email("admin@mdmanager.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // 添加安全认证
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .schemaRequirement("Authorization",
                        new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .description("JWT Token认证，格式：Bearer {token}"));
    }
}
