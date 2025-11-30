package com.springboot.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置类
 *
 * <p>主要作用：</p>
 * <ul>
 *   <li>定义 API 文档的基本信息（标题、描述、版本）</li>
 *   <li>配置 JWT 安全认证方案，让 Swagger UI 页面显示 Authorize 按钮</li>
 *   <li>所有需要认证的接口会自动带上 Authorization: Bearer xxx</li>
 * </ul>
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置 OpenAPI 文档信息和安全认证
     *
     * @return OpenAPI 对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 定义安全方案：JWT Bearer Token
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization") // 请求头字段名
                .type(SecurityScheme.Type.HTTP) // 类型：HTTP
                .scheme("bearer") // 认证方式：Bearer
                .bearerFormat("JWT"); // 格式：JWT

        // 定义安全需求：所有接口默认需要 Authorization
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization");

        return new OpenAPI()
                .info(new Info()
                        .title("SpringBoot Admin API")
                        .description("后台管理系统接口文档，支持 JWT 认证")
                        .version("v1.0.0"))
                .addSecurityItem(securityRequirement)
                .schemaRequirement("Authorization", securityScheme);
    }
}
