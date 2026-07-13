package cl.duocuc.transportistaservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transportistaOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Transportista Service API")
                .description("API para administrar transportistas y datos de despacho en el sistema Casa de la Impresión.")
                .version("v1"));
    }
}
