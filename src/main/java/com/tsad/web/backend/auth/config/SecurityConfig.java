package com.tsad.web.backend.auth.config;

import com.tsad.web.backend.auth.AllRequestFilter;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/member/**").hasAnyRole("MEMBER", "ADMIN", "MASTER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MASTER")
                        .requestMatchers("/master/**").hasAnyRole("MASTER")
                        .requestMatchers("/payment/**").authenticated()
                        .anyRequest().denyAll()
                ).addFilterBefore(new AllRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}