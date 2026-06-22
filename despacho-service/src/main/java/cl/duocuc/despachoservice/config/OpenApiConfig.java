package cl.duocuc.despachoservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI despachoOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("despacho-service API")
                .description("API para la gestión de despachos de pedidos")
                .version("1.0.0")
                .license(new License().name("Uso Educativo DSY1103")));
    }
}
