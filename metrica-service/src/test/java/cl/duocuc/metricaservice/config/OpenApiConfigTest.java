package cl.duocuc.metricaservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

    @Test
    void metricaOpenAPICreaMetadataDelServicio() {
        // Given
        OpenApiConfig config = new OpenApiConfig();

        // When
        OpenAPI openAPI = config.metricaOpenAPI();

        // Then
        assertEquals("metrica-service API", openAPI.getInfo().getTitle());
        assertEquals("API para métricas de clientes, productos y ventas", openAPI.getInfo().getDescription());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertEquals("Uso Educativo DSY1103", openAPI.getInfo().getLicense().getName());
    }
}
