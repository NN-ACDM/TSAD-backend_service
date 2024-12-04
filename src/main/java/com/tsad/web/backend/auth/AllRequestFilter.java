package com.tsad.web.backend.auth;

import com.tsad.web.backend.repository.webservicedb.jpa.UserAuthJpaRepository;
import com.tsad.web.backend.repository.webservicedb.jpa.model.UserAuthJpaEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class AllRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserAuthJpaRepository userAuthJpaRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPathUrl = request.getRequestURI();
        return requestPathUrl.startsWith("/public") || requestPathUrl.startsWith("/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestToken = this.extractToken(request.getHeader("Authorization"));

        if (this.validateRequestByToken(requestToken)) {
            String username = this.extractUsernameFromToken(requestToken);
            List<String> roles = this.extractRolesFromToken(requestToken);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean validateRequestByToken(String token) {
        if (ObjectUtils.isEmpty(token)) {
            return false;
        }
        Optional<UserAuthJpaEntity> userOpt = userAuthJpaRepository.findByToken(token);
        return userOpt.isPresent();
    }

    private String extractUsernameFromToken(String token) {
        // Implement logic to extract username from token
        return "user";
    }

    private List<String> extractRolesFromToken(String token) {
        // Implement logic to extract roles from token
        return List.of("USER");
    }
}