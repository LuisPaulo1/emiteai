package com.emiteai.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuditingFilter> auditingFilter() {
        FilterRegistrationBean<AuditingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuditingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}