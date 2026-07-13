package cl.duocuc.authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Auth Service API")
                .description("API de autenticación y gestión de usuarios para el sistema Casa de la Impresión.")
                .version("v1"));
    }
}
