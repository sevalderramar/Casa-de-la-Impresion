package cl.duocuc.logservice.controller;

import cl.duocuc.logservice.config.GlobalExceptionHandler;
import cl.duocuc.logservice.dto.ApiResponse;
import cl.duocuc.logservice.entity.LogEntrada;
import cl.duocuc.logservice.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogService logService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new LogController(logService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void registrarLogRetornaCreated() throws Exception {
        // Given
        LogEntrada entrada = log("pedido-service", LocalDateTime.of(2026, 7, 1, 10, 0));
        when(logService.registrarLog(any())).thenReturn(ApiResponse.success("Log registrado correctamente", entrada));

        // When / Then
        mockMvc.perform(post("/api/logs")
                        .contentType("application/json")
                        .content("{\"servicio\":\"pedido-service\",\"operacion\":\"CREAR_PEDIDO\",\"usuarioId\":\"42\",\"resultado\":\"OK\",\"detalle\":\"Pedido creado\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Log registrado correctamente"))
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.servicio").value("pedido-service"));

        verify(logService).registrarLog(any());
    }

    @Test
    void registrarLogRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/logs")
                        .contentType("application/json")
                        .content("{\"servicio\":\"\",\"operacion\":\"\",\"resultado\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(logService);
    }

    @Test
    void consultarLogsSinFiltrosRetornaListado() throws Exception {
        // Given
        when(logService.consultarLogs(null, null))
                .thenReturn(ApiResponse.success("Logs consultados correctamente", List.of(log("pedido-service", LocalDateTime.now()))));

        // When / Then
        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Logs consultados correctamente"))
                .andExpect(jsonPath("$.data[0].servicio").value("pedido-service"));

        verify(logService).consultarLogs(null, null);
    }

    @Test
    void consultarLogsConFiltrosConvierteFechaIso() throws Exception {
        // Given
        LocalDateTime desde = LocalDateTime.of(2026, 7, 1, 10, 30);
        when(logService.consultarLogs(eq("pedido-service"), eq(desde)))
                .thenReturn(ApiResponse.success("Logs consultados correctamente", List.of(log("pedido-service", desde))));

        // When / Then
        mockMvc.perform(get("/api/logs")
                        .param("servicio", "pedido-service")
                        .param("desde", "2026-07-01T10:30:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].servicio").value("pedido-service"));

        verify(logService).consultarLogs("pedido-service", desde);
    }

    @Test
    void pingRetornaServicioOk() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/logs/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("log-service OK"))
                .andExpect(jsonPath("$.data").value("log-service OK"))
                .andExpect(jsonPath("$.exitoso").value(true));
    }

    private LogEntrada log(String servicio, LocalDateTime timestamp) {
        LogEntrada entrada = new LogEntrada();
        entrada.setId(1L);
        entrada.setServicio(servicio);
        entrada.setOperacion("CREAR_PEDIDO");
        entrada.setUsuarioId("42");
        entrada.setResultado("OK");
        entrada.setDetalle("Pedido creado");
        entrada.setTimestamp(timestamp);
        return entrada;
    }
}
