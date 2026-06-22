package cl.duocuc.productoservice.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI productoOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("producto-service API")
                .description("API para la gestión del catálogo de productos")
                .version("1.0.0")
                .license(new License().name("Uso Educativo DSY1103")));
    }
}
