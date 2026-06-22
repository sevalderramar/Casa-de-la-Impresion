package cl.duocuc.metricaservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI metricaOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("metrica-service API")
                .description("API para métricas de clientes, productos y ventas")
                .version("1.0.0")
                .license(new License().name("Uso Educativo DSY1103")));
    }
}
