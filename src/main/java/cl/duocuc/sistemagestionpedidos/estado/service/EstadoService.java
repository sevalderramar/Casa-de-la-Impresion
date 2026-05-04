package cl.duocuc.sistemagestionpedidos.estado.service;

import cl.duocuc.sistemagestionpedidos.estado.dto.CambioEstadoRequest;
import cl.duocuc.sistemagestionpedidos.estado.dto.CambioEstadoResponse;
import cl.duocuc.sistemagestionpedidos.estado.model.CambioEstado;
import cl.duocuc.sistemagestionpedidos.estado.repository.CambioEstadoRepository;
import cl.duocuc.sistemagestionpedidos.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoService {

    private final CambioEstadoRepository cambioEstadoRepository;

    public EstadoService(CambioEstadoRepository cambioEstadoRepository) {
        this.cambioEstadoRepository = cambioEstadoRepository;
    }

    /**
     * Registra un nuevo cambio de estado para un pedido.
     */
    public CambioEstadoResponse registrarCambioEstado(CambioEstadoRequest request) {
        CambioEstado cambio = new CambioEstado();
        cambio.setNumeroPedido(request.getNumeroPedido());
        cambio.setEstadoAnterior(request.getEstadoAnterior());
        cambio.setEstadoNuevo(request.getEstadoNuevo());
        cambio.setObservacion(request.getObservacion());
        cambio.setFechaCambio(LocalDateTime.now());

        CambioEstado cambioGuardado = cambioEstadoRepository.save(cambio);
        return mapearAResponse(cambioGuardado);
    }

    /**
     * Lista todos los cambios de estado de un pedido, ordenados por fecha ascendente.
     */
    public List<CambioEstadoResponse> listarCambiosPorPedido(Long numeroPedido) {
        List<CambioEstado> cambios = cambioEstadoRepository.findByNumeroPedidoOrderByFechaCambioAsc(numeroPedido);

        if (cambios.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron cambios de estado para el pedido con numero: " + numeroPedido);
        }

        return cambios.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el último cambio de estado de un pedido.
     */
    public CambioEstadoResponse obtenerUltimoEstadoPorPedido(Long numeroPedido) {
        List<CambioEstado> cambios = cambioEstadoRepository.findByNumeroPedidoOrderByFechaCambioAsc(numeroPedido);

        if (cambios.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron cambios de estado para el pedido con numero: " + numeroPedido);
        }

        CambioEstado ultimoCambio = cambios.get(cambios.size() - 1);
        return mapearAResponse(ultimoCambio);
    }

    /**
     * Mapea una entidad CambioEstado a su DTO response.
     */
    private CambioEstadoResponse mapearAResponse(CambioEstado cambio) {
        return new CambioEstadoResponse(
                cambio.getId(),
                cambio.getNumeroPedido(),
                cambio.getEstadoAnterior(),
                cambio.getEstadoNuevo(),
                cambio.getFechaCambio(),
                cambio.getObservacion()
        );
    }
}

