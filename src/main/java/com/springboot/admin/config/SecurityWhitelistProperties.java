package com.springboot.admin.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 安全白名单配置类
 * 从 application.yml 中读取 security.whitelist 配置
 */
@Getter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityWhitelistProperties {

    private List<String> whitelist;

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }
}
