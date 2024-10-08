package com.minjer.smartchill.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("*") // 允许的源
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的方法
                .allowCredentials(true); // 是否允许带凭证

        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("*") // 允许的源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                .allowedHeaders("*") // 允许的请求头
                .exposedHeaders("Authorization") // 允许的响应头
                .allowCredentials(true); // 是否允许带凭证
    }


}
