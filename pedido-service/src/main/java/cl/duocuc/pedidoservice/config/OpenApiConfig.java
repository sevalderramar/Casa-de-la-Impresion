package cl.duocuc.pedidoservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pedidoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pedido Service API")
                        .description("Microservicio encargado de la gestión de pedidos")
                        .version("1.0.0")
                        .license(new License().name("Test Docker")));

    }
}