package com.tsad.web.backend.config;

import com.tsad.web.backend.config.handler.RequestFilterHandler;
import com.tsad.web.backend.service.authentication.CredentialService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final CredentialService credentialService;

    public FilterConfig(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @Bean
    public RequestFilterHandler customRequestFilter() {
        return new RequestFilterHandler(credentialService);
    }
}
