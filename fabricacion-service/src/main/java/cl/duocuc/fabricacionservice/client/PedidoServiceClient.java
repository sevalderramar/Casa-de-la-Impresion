package cl.duocuc.fabricacion.client;

import cl.duocuc.fabricacion.exception.FabricacionException;
import cl.duocuc.fabricacion.exception.PedidoNoEncontradoException;
import cl.duocuc.fabricacion.exception.ServiceUnavailableException;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class PedidoServiceClient {

    private final PedidoFeignClient feign;

    public void validarExistencia(Long numeroPedido) {
        try {
            feign.obtenerPedido(numeroPedido);
        } catch (FeignException.NotFound e) {
            throw new PedidoNoEncontradoException("Pedido no encontrado: " + numeroPedido);
        } catch (FeignException.BadRequest e) {
            throw new FabricacionException(buildMessage("pedido-service rechaza la solicitud", e));
        } catch (FeignException.Unauthorized | FeignException.Forbidden e) {
            throw new ServiceUnavailableException("pedido-service no permite la validacion", e);
        } catch (RetryableException e) {
            throw new ServiceUnavailableException(buildMessage("No fue posible conectar con pedido-service", e), e);
        } catch (FeignException e) {
            throw new ServiceUnavailableException(buildMessage("No fue posible comunicarse con pedido-service", e), e);
        } catch (RuntimeException e) {
            throw new ServiceUnavailableException("Error inesperado al validar el pedido", e);
        }
    }

    public void notificarPedidoEnFabricacion(Long numeroPedido) {
        try {
            PedidoFeignClient.UpdateEstadoRequest req = new PedidoFeignClient.UpdateEstadoRequest("PRODUCCION");
            feign.actualizarEstado(numeroPedido, req);
        } catch (FeignException.NotFound e) {
            throw new PedidoNoEncontradoException("Pedido no encontrado al iniciar fabricacion: " + numeroPedido);
        } catch (FeignException.Conflict e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, buildMessage("pedido-service rechaza el inicio de fabricacion", e), e);
        } catch (FeignException.BadRequest e) {
            throw new FabricacionException(buildMessage("pedido-service rechaza el inicio de fabricacion", e));
        } catch (RetryableException e) {
            throw new ServiceUnavailableException(buildMessage("No fue posible conectar con pedido-service al iniciar fabricacion", e), e);
        } catch (FeignException e) {
            throw new ServiceUnavailableException(buildMessage("No fue posible notificar a pedido-service", e), e);
        }
    }

    public void notificarPedidoListo(Long numeroPedido) {
        try {
            PedidoFeignClient.UpdateEstadoRequest req = new PedidoFeignClient.UpdateEstadoRequest("LISTO");
            feign.actualizarEstado(numeroPedido, req);
        } catch (FeignException.NotFound e) {
            throw new PedidoNoEncontradoException("Pedido no encontrado al notificar: " + numeroPedido);
        } catch (FeignException.Conflict e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, buildMessage("pedido-service rechaza el cierre de fabricacion", e), e);
        } catch (FeignException.BadRequest e) {
            throw new FabricacionException(buildMessage("pedido-service rechaza el cambio de estado", e));
        } catch (RetryableException e) {
            throw new ServiceUnavailableException(buildMessage("No fue posible conectar con pedido-service al notificar", e), e);
        } catch (FeignException e) {
            throw new ServiceUnavailableException(buildMessage("No fue posible notificar a pedido-service", e), e);
        } catch (RuntimeException e) {
            throw new ServiceUnavailableException("Error inesperado al notificar el pedido", e);
        }
    }

    private String buildMessage(String prefix, FeignException exception) {
        String body = exception.contentUTF8();
        if (body != null && !body.isBlank()) {
            return prefix + ": " + body;
        }

        return prefix + " (HTTP " + exception.status() + ")";
    }

    private String buildMessage(String prefix, RetryableException exception) {
        String detail = exception.getMessage();
        if (detail != null && !detail.isBlank()) {
            return prefix + ": " + detail;
        }

        return prefix;
    }
}
