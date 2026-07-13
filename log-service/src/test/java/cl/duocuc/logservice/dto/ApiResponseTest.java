package cl.duocuc.logservice.dto;

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
    void successConstruyeRespuestaExitosa() {
        // When
        ApiResponse<String> response = ApiResponse.success("ok", "data");

        // Then
        assertEquals("ok", response.getMensaje());
        assertEquals("data", response.getData());
        assertTrue(response.isExitoso());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void errorSinDataConstruyeRespuestaFallida() {
        // When
        ApiResponse<String> response = ApiResponse.error("error");

        // Then
        assertEquals("error", response.getMensaje());
        assertNull(response.getData());
        assertFalse(response.isExitoso());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void errorConDataConstruyeRespuestaFallidaConDetalle() {
        // When
        ApiResponse<String> response = ApiResponse.error("error", "detalle");

        // Then
        assertEquals("error", response.getMensaje());
        assertEquals("detalle", response.getData());
        assertFalse(response.isExitoso());
        assertNotNull(response.getTimestamp());
    }
}
