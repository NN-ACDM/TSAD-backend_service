package com.tsad.web.backend.config.handler;

import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import com.tsad.web.backend.service.authentication.CredentialService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            UserAuthJpaEntity user = credentialService.validateUsernameAndToken(
                    request.getHeader(RequestHeaderName.USERNAME),
                    request.getHeader(HttpHeaders.AUTHORIZATION));
            if (!ObjectUtils.isEmpty(user)) {
                List<String> roles = Collections.singletonList(user.getLevel());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                user.getToken(),
                                roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("doFilterInternal() ... SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
            }
        } catch (BusinessException e) {
            log.debug("doFilterInternal() ... validate credential failed");
            return;
        }

        filterChain.doFilter(request, response);
    }
}