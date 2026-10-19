package com.example.LatteList.config;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Documentacion interactiva en /swagger-ui.html (con boton "Authorize" para pegar el JWT). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI latteListOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LatteList API")
                        .version("1.0")
                        .description("API REST para descubrir, reseñar y organizar cafés de Mar del Plata"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
