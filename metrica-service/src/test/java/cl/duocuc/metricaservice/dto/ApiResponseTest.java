package cl.duocuc.metricaservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ApiResponseTest {

    @Test
    void successCreaRespuestaExitosaConData() {
        // When
        ApiResponse<String> response = ApiResponse.success("ok", "data");

        // Then
        assertEquals("ok", response.mensaje());
        assertEquals("data", response.data());
        assertTrue(response.exitoso());
        assertNotNull(response.timestamp());
    }

    @Test
    void errorCreaRespuestaNoExitosaSinData() {
        // When
        ApiResponse<String> response = ApiResponse.error("error");

        // Then
        assertEquals("error", response.mensaje());
        assertNull(response.data());
        assertFalse(response.exitoso());
        assertNotNull(response.timestamp());
    }
}
