package com.tsad.web.backend.config.handler;

import com.tsad.web.backend.service.authentication.CredentialService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class HeaderInterceptorHandler implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(HeaderInterceptorHandler.class);

    @Autowired
    private CredentialService credentialService;

    @Override
    public boolean preHandle(@Nullable HttpServletRequest request,
                             @Nullable HttpServletResponse response,
                             @Nullable Object handler) {
        return true;
    }

    @Override
    public void postHandle(@Nullable HttpServletRequest request,
                           @Nullable HttpServletResponse response,
                           @Nullable Object handler,
                           ModelAndView modelAndView) {
        // HandlerInterceptor Interception point after successful execution of a handler.
        // Called after HandlerAdapter actually invoked the handler,
        // but before the DispatcherServlet renders the view. (optional)
    }

    @Override
    public void afterCompletion(@Nullable HttpServletRequest request,
                                @Nullable HttpServletResponse response,
                                @Nullable Object handler, Exception ex) {
        // Post-processing after the response is sent (optional)
    }
}
