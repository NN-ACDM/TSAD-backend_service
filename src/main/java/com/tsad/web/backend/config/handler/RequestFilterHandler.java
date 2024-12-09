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
import jakarta.validation.constraints.NotNull;
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
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @Nullable HttpServletResponse response,
                                    @Nullable FilterChain filterChain) throws ServletException, IOException {
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
        } catch (BusinessException ex) {
            if (!ObjectUtils.isEmpty(response)) {
                this.setErrorResponse(response, ex);
            }
            return;
        }

        if (filterChain != null) {
            filterChain.doFilter(request, response);
        } else {
            log.error("doFilterInternal() ... filterChain is Null");
        }
    }

    private void setErrorResponse(HttpServletResponse response, BusinessException ex) throws IOException {
        response.setHeader("Accept", "application/json");
        response.setHeader("Content-type", "application/json");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", ex.getMessage());
        jsonObject.put("code", ex.getErrorCode());
        response.getWriter().write(jsonObject.toString());
        response.getWriter().flush();
    }
}