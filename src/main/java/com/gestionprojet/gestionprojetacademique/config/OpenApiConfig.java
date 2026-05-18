package com.gestionprojet.gestionprojetacademique.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Gestion des Projets Académiques — ISMAGI")
                .version("1.0.0")
                .description("API REST pour la gestion des PFE/PFA: projets, rapports, séances, soutenances.")
                .contact(new Contact().name("ISMAGI").email("contact@ismagi.ma")));
    }
}
