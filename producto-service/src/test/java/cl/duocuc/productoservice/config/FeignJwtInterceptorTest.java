package cl.duocuc.productoservice.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FeignJwtInterceptorTest {

    private final FeignJwtInterceptor interceptor = new FeignJwtInterceptor();

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void applyPropagaAuthorizationCuandoExisteEnRequestContext() {
        // Given
        String authorization = "Bearer token-valido";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, authorization);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        // When
        interceptor.apply(template);

        // Then
        assertEquals(List.of(authorization), List.copyOf(template.headers().get(HttpHeaders.AUTHORIZATION)));
    }

    @Test
    void applyNoAgregaAuthorizationCuandoNoExisteRequestContext() {
        // Given
        RequestContextHolder.resetRequestAttributes();
        RequestTemplate template = new RequestTemplate();

        // When
        interceptor.apply(template);

        // Then
        assertFalse(template.headers().containsKey(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void applyNoAgregaAuthorizationCuandoRequestNoTieneHeader() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        // When
        interceptor.apply(template);

        // Then
        assertFalse(template.headers().containsKey(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void applyNoAgregaAuthorizationCuandoHeaderEstaEnBlanco() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "   ");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        // When
        interceptor.apply(template);

        // Then
        assertFalse(template.headers().containsKey(HttpHeaders.AUTHORIZATION));
    }
}
