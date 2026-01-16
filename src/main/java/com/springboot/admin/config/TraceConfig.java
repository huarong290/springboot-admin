package com.springboot.admin.config;


import com.springboot.admin.filter.TraceIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TraceId Filter 配置
 */
@Configuration
public class TraceConfig {

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<TraceIdFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new TraceIdFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(1); // 越小越靠前
        return bean;
    }
}
