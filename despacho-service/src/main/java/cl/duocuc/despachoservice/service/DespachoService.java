package cl.duocuc.despachoservice.service;

import cl.duocuc.despachoservice.client.PedidoFeignClient;
import cl.duocuc.despachoservice.common.exception.ConflictException;
import cl.duocuc.despachoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.despachoservice.common.exception.ServiceUnavailableException;
import cl.duocuc.despachoservice.dto.DespachoRequest;
import cl.duocuc.despachoservice.dto.DespachoResponse;
import cl.duocuc.despachoservice.model.Despacho;
import cl.duocuc.despachoservice.repository.DespachoRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class DespachoService {

    private static final Set<String> TIPOS_VALIDOS = Set.of("RETIRO", "RM", "REGION");

    private final DespachoRepository despachoRepository;
    private final PedidoFeignClient pedidoFeignClient;

    public DespachoService(DespachoRepository despachoRepository, PedidoFeignClient pedidoFeignClient) {
        this.despachoRepository = despachoRepository;
        this.pedidoFeignClient = pedidoFeignClient;
    }

    public DespachoResponse crearDespacho(DespachoRequest request) {
        Long numeroPedido = request.getNumeroPedido();
        validarPedidoExiste(numeroPedido);

        if (despachoRepository.existsByNumeroPedido(numeroPedido)) {
            throw new ConflictException("Ya existe un despacho registrado para el pedido " + numeroPedido);
        }

        Despacho despacho = new Despacho();
        despacho.setNumeroPedido(numeroPedido);
        despacho.setTipoDespacho(normalizarTipoDespacho(request.getTipoDespacho()));
        despacho.setTransportista(normalizarTexto(request.getTransportista()));
        despacho.setFechaDespacho(LocalDateTime.now());
        despacho.setTrackingCode(normalizarTexto(request.getTrackingCode()));

        return convertirAResponse(despachoRepository.save(despacho));
    }

    public List<DespachoResponse> listarDespachos(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return despachoRepository.findAll().stream()
                    .map(this::convertirAResponse)
                    .toList();
        }

        String tipoNormalizado = normalizarTipoDespacho(tipo);
        return despachoRepository.findByTipoDespacho(tipoNormalizado).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public DespachoResponse obtenerDespachoPorNumeroPedido(Long numeroPedido) {
        Despacho despacho = despachoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado para el pedido " + numeroPedido));
        return convertirAResponse(despacho);
    }

    public DespachoResponse actualizarDespacho(Long id, DespachoRequest request) {
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con ID " + id));

        Long nuevoNumeroPedido = request.getNumeroPedido();
        validarPedidoExiste(nuevoNumeroPedido);

        if (!despacho.getNumeroPedido().equals(nuevoNumeroPedido)) {
            despachoRepository.findByNumeroPedido(nuevoNumeroPedido)
                    .filter(existente -> !existente.getId().equals(id))
                    .ifPresent(existente -> {
                        throw new ConflictException("Ya existe un despacho registrado para el pedido " + nuevoNumeroPedido);
                    });
        }

        despacho.setNumeroPedido(nuevoNumeroPedido);
        despacho.setTipoDespacho(normalizarTipoDespacho(request.getTipoDespacho()));
        despacho.setTransportista(normalizarTexto(request.getTransportista()));
        despacho.setTrackingCode(normalizarTexto(request.getTrackingCode()));

        return convertirAResponse(despachoRepository.save(despacho));
    }

    private void validarPedidoExiste(Long numeroPedido) {
        try {
            pedidoFeignClient.obtenerPedidoPorNumero(numeroPedido);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Pedido no encontrado con número " + numeroPedido);
        } catch (FeignException ex) {
            throw new ServiceUnavailableException("No se pudo validar el pedido en pedido-service", ex);
        }
    }

    private String normalizarTipoDespacho(String tipoDespacho) {
        String tipoNormalizado = normalizarTexto(tipoDespacho).toUpperCase();
        if (!TIPOS_VALIDOS.contains(tipoNormalizado)) {
            throw new IllegalArgumentException("tipoDespacho debe ser RETIRO, RM o REGION");
        }
        return tipoNormalizado;
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return null;
        }
        return texto.trim();
    }

    private DespachoResponse convertirAResponse(Despacho despacho) {
        return new DespachoResponse(
                despacho.getId(),
                despacho.getNumeroPedido(),
                despacho.getTipoDespacho(),
                despacho.getTransportista(),
                despacho.getFechaDespacho(),
                despacho.getTrackingCode()
        );
    }
}

