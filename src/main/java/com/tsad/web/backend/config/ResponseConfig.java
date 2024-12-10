//package com.tsad.web.backend.config;
//
//import com.tsad.web.backend.config.handler.HeaderInterceptorHandler;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class ResponseConfig implements WebMvcConfigurer {
//
//        private final HeaderInterceptorHandler headerInterceptorHandler;
//
//    public ResponseConfig(HeaderInterceptorHandler headerInterceptorHandler) {
//        this.headerInterceptorHandler = headerInterceptorHandler;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(headerInterceptorHandler).addPathPatterns("/**");
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/resources/**")
//                .addResourceLocations("classpath:/static/")
//                .setCachePeriod(3600);
//
//        // Ensure /admin paths are not treated as static resources
//        registry.addResourceHandler("/admin/**").addResourceLocations("classpath:/static/admin/");
//    }
//}
