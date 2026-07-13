package cl.duocuc.logservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI logOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Log Service API")
                .description("API para registrar y consultar eventos del sistema Casa de la Impresión.")
                .version("v1"));
    }
}
