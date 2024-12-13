package com.tsad.web.backend.config.handler;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import com.tsad.web.backend.service.authentication.CredentialService;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class RequestFilterHandler extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestFilterHandler.class);
    private final CredentialService credentialService;

    public RequestFilterHandler(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPathUrl = request.getRequestURI();
        return requestPathUrl.startsWith("/public") || requestPathUrl.equals("/login");
    }

    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request,
                                    @Nullable HttpServletResponse response,
                                    @Nullable FilterChain filterChain) throws ServletException, IOException {
        try {
            assert request != null;
            UserAuthJpaEntity user = credentialService.validateUsernameAndToken(
                    request.getHeader(RequestHeaderName.USERNAME),
                    request.getHeader(HttpHeaders.AUTHORIZATION));
            if (!ObjectUtils.isEmpty(user)) {
                List<String> roles = Collections.singletonList(user.getLevel());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getId(),
                                user.getToken(),
                                roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("doFilterInternal() ... SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
            }
        } catch (BusinessException ex) {
            assert response != null;
            response.setHeader("Accept", "application/json");
            response.setHeader("Content-type", "application/json");
            response.setStatus(ex.getHttpStatus().value());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", ex.getMessage());
            jsonObject.put("code", ex.getErrorCode());
            response.getWriter().write(jsonObject.toString());
            response.getWriter().flush();
            return;
        }
        assert filterChain != null;
        filterChain.doFilter(request, response);
    }
}