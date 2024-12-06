package com.tsad.web.backend.config.handler;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.service.authentication.CredentialService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class HeaderInterceptorHandler implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(HeaderInterceptorHandler.class);

    private final CredentialService credentialService;

    public HeaderInterceptorHandler(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @Override
    public boolean preHandle(@Nullable HttpServletRequest request,
                             @Nullable HttpServletResponse response,
                             @Nullable Object handler) throws BusinessException {
        if (ObjectUtils.isEmpty(request)) {
            return false;
        }
        String requestUri = request.getRequestURI();
        if (!ObjectUtils.isEmpty(response)) {
            if (requestUri.startsWith("/member") ||
                    requestUri.startsWith("/admin") ||
                    requestUri.startsWith("/master")) {
                String newToken = credentialService.rotateToken(
                        request.getHeader(RequestHeaderName.USERNAME),
                        request.getHeader(HttpHeaders.AUTHORIZATION));
                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newToken);
            }
            log.debug("preHandle() ... response: {}", response.getHeader(HttpHeaders.AUTHORIZATION));
        }
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
