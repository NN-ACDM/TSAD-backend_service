package com.tsad.web.backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CustomHeaderInterceptor implements HandlerInterceptor {

    @Autowired
    private CredentialService credentialService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        String requestUri = request.getRequestURI();
        String currentToken = response.getHeader("X-Auth-Token");
        String newToken = credentialService.rotateToken(currentToken);
        response.setHeader("X-Auth-Token", newToken);
    }
}
