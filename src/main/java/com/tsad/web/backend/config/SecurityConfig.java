package com.tsad.web.backend.config;

import com.tsad.web.backend.config.handler.RequestFilterHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RequestFilterHandler requestFilterHandler;

    public SecurityConfig(RequestFilterHandler requestFilterHandler) {
        this.requestFilterHandler = requestFilterHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/logout").hasAnyRole("MEMBER", "ADMIN", "MASTER")
                        .requestMatchers("/auth/**").hasAnyRole("MEMBER", "ADMIN", "MASTER")
                        .requestMatchers("/member/**").hasAnyRole("MEMBER", "ADMIN", "MASTER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MASTER")
                        .requestMatchers("/master/**").hasAnyRole("MASTER")
                        .requestMatchers("/payment/**").authenticated()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().permitAll()
                ).addFilterBefore(requestFilterHandler, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.permitAll().disable());
        return http.build();
    }
}
