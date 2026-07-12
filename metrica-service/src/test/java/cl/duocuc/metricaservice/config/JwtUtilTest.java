package cl.duocuc.metricaservice.config;

import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private static final String TEST_SECRET = Encoders.BASE64.encode(
            "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));

    @Test
    void generarTokenPermiteValidarYExtraerClaims() {
        // Given
        JwtUtil jwtUtil = jwtUtilConExpiracion(60_000L);

        // When
        String token = jwtUtil.generarToken("usuario@test.cl", "ADMIN");

        // Then
        assertTrue(jwtUtil.esValido(token));
        assertEquals("usuario@test.cl", jwtUtil.extraerUsername(token));
        assertEquals("usuario@test.cl", jwtUtil.extraerEmail(token));
        assertEquals("ADMIN", jwtUtil.extraerRol(token));
        assertEquals(60_000L, jwtUtil.getExpirationMs());
    }

    @Test
    void esValidoRetornaFalseCuandoTokenEsInvalido() {
        // Given
        JwtUtil jwtUtil = jwtUtilConExpiracion(60_000L);

        // When
        boolean valido = jwtUtil.esValido("token-invalido");

        // Then
        assertFalse(valido);
    }

    @Test
    void esValidoRetornaFalseCuandoTokenEstaExpirado() throws InterruptedException {
        // Given
        JwtUtil jwtUtil = jwtUtilConExpiracion(1L);
        String token = jwtUtil.generarToken("usuario@test.cl", "USER");
        Thread.sleep(5L);

        // When
        boolean valido = jwtUtil.esValido(token);

        // Then
        assertFalse(valido);
    }

    private JwtUtil jwtUtilConExpiracion(long expirationMs) {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", expirationMs);
        return jwtUtil;
    }
}
