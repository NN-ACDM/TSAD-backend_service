package com.tsad.web.backend.auth.config;

import com.tsad.web.backend.auth.CustomHeaderInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResponseConfig implements WebMvcConfigurer {

    @Autowired
    private CustomHeaderInterceptor customHeaderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customHeaderInterceptor);
    }
}
