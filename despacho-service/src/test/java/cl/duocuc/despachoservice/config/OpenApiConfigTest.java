package cl.duocuc.despachoservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

    @Test
    void despachoOpenAPICreaMetadataDelServicio() {
        // Given
        OpenApiConfig config = new OpenApiConfig();

        // When
        OpenAPI openAPI = config.despachoOpenAPI();

        // Then
        assertEquals("despacho-service API", openAPI.getInfo().getTitle());
        assertEquals("API para la gestión de despachos de pedidos", openAPI.getInfo().getDescription());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertEquals("Uso Educativo DSY1103", openAPI.getInfo().getLicense().getName());
    }
}
