package hospital.management.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;
@Configuration

public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080")
                ))
                .info(new Info()
                        .title("User Service API")
                        .description("User Authentication and Management Service for Hospital Management System")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Hospital Management Team")
                                .email("support@hospital.local")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Auth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for API authentication")));
    }
}

