package cl.duocuc.authservice.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class FeignJwtInterceptorTest {

    private final FeignJwtInterceptor interceptor = new FeignJwtInterceptor();

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void applyNoAgregaHeaderCuandoNoHayRequestActual() {
        // Given
        RequestTemplate template = new RequestTemplate();

        // When
        interceptor.apply(template);

        // Then
        assertFalse(template.headers().containsKey(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void applyPropagaAuthorizationCuandoExisteEnRequestActual() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        // When
        interceptor.apply(template);

        // Then
        assertEquals("Bearer token", template.headers().get(HttpHeaders.AUTHORIZATION).iterator().next());
    }

    @Test
    void applyIgnoraAuthorizationVacio() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, " ");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        // When
        interceptor.apply(template);

        // Then
        assertFalse(template.headers().containsKey(HttpHeaders.AUTHORIZATION));
    }
}
