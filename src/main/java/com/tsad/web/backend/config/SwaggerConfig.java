package com.tsad.web.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "My API",
                version = "1.0",
                description = "API Documentation"
        ),
        servers = {
                @Server(url = "http://localhost:8090", description = "Local Server"),
                @Server(url = "https://www.astral-containers.com/tsad/api", description = "Development Server")
        }
)
@Configuration
public class SwaggerConfig {
}
