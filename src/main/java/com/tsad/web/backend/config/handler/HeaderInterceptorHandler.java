package com.tsad.web.backend.config.handler;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.service.authentication.CredentialService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class HeaderInterceptorHandler implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(HeaderInterceptorHandler.class);

    @Autowired
    private CredentialService credentialService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws BusinessException {
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/member") ||
                requestUri.startsWith("/admin") ||
                requestUri.startsWith("/master")) {
            String newToken = credentialService.rotateToken(
                    request.getHeader(RequestHeaderName.USERNAME),
                    request.getHeader(HttpHeaders.AUTHORIZATION));
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newToken);
        }
        log.debug("preHandle() ... response: {}", response.getHeader(HttpHeaders.AUTHORIZATION));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        // HandlerInterceptor Interception point after successful execution of a handler.
        // Called after HandlerAdapter actually invoked the handler,
        // but before the DispatcherServlet renders the view. (optional)
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) {
        // Post-processing after the response is sent (optional)
    }
}
