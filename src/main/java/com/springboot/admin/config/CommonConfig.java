package com.springboot.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CommonConfig {

    /**
     * 注册一个 BCryptPasswordEncoder Bean，
     * 这样容器里既有 BCryptPasswordEncoder 类型的 Bean，
     * 也有其接口 PasswordEncoder 类型的 Bean，供全局使用。
     */

    // （可选）如果你习惯面向接口注入，下面这个也能同时提供 PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}

