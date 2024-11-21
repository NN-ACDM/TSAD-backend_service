package com.tsad.web.backend.config.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OneTimeTokenFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        boolean isChangeToken = true;

        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/public")) {
            isChangeToken = false;
        }

        if ("/tsad/auth/logout".equals(requestUri)) {
            isChangeToken = false;
        }

        if (isChangeToken) {
            String rqToken = request.getHeader("Authorization");

            if (rqToken != null && rqToken.startsWith("Bearer ")) {
                rqToken = rqToken.substring(7);
                try {
                    tokenService.validateAndConsumeToken(rqToken);
                } catch (IllegalArgumentException e) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Invalid or expired token");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}