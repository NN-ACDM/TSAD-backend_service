package com.tsad.web.backend.config.handler;

import com.tsad.web.backend.common.JwtUtils;
import com.tsad.web.backend.common.RequestHeaderName;
import com.tsad.web.backend.config.BusinessException;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import com.tsad.web.backend.service.authentication.CredentialService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
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

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CredentialService credentialService;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return true;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            UserAuthJpaEntity user = credentialService.validateUsernameAndToken(
                    request.getHeader(RequestHeaderName.USERNAME),
                    request.getHeader(HttpHeaders.AUTHORIZATION));
            if (!ObjectUtils.isEmpty(user)) {
                List<String> roles = Collections.singletonList(user.getLevel());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getId(),
                                user.getAccessToken(),
                                roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            log.debug("doFilterInternal() ... SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
        } catch (BusinessException ex) {
            log.error("doFilterInternal() ... Error: {}", ex.getMessage(), ex);
            response.setHeader("Accept", "application/json");
            response.setHeader("Content-type", "application/json");
            response.setStatus(ex.getHttpStatus().value());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", ex.getMessage());
            jsonObject.put("code", ex.getErrorCode());
            response.getWriter().write(jsonObject.toString());
            response.getWriter().flush();
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}