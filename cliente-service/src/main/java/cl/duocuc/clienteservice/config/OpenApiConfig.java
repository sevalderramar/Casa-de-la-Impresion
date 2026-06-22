package cl.duocuc.clienteservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clienteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("cliente-service API")
                        .description("API para la gestión de clientes de Casa de la Impresión")
                        .version("1.0.0")
                        .license(new License().name("Uso Educativo DSY1103"))
                        .contact(new Contact().name("Casa de la Impresión")))
                .externalDocs(new ExternalDocumentation().description("Documentación del microservicio cliente"));
    }
}
