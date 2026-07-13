package cl.duocuc.estadoservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI estadoOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Estado Service API")
                .description("API para registrar y consultar cambios de estado de pedidos en el sistema Casa de la Impresión.")
                .version("v1"));
    }
}
