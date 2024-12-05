package com.tsad.web.backend.config;

import com.tsad.web.backend.config.handler.HeaderInterceptorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResponseConfig implements WebMvcConfigurer {

    @Autowired
    private HeaderInterceptorHandler headerInterceptorHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerInterceptorHandler);
    }
}
