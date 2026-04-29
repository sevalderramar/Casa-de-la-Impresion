package cl.duocuc.sistemagestionpedidos.cliente.service;

import cl.duocuc.sistemagestionpedidos.cliente.dto.ClienteRequest;
import cl.duocuc.sistemagestionpedidos.cliente.dto.ClienteResponse;
import cl.duocuc.sistemagestionpedidos.cliente.model.Cliente;
import cl.duocuc.sistemagestionpedidos.cliente.repository.ClienteRepository;
import cl.duocuc.sistemagestionpedidos.common.exception.ConflictException;
import cl.duocuc.sistemagestionpedidos.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClienteService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_INACTIVO = "INACTIVO";

    private final ClienteRepository clienteRepository;
    private final Map<Long, Cliente> cacheClientes = new ConcurrentHashMap<>();

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponse crearCliente(ClienteRequest request) {
        String rutNormalizado = normalizarTexto(request.getRut());

        if (clienteRepository.existsByRut(rutNormalizado)) {
            throw new ConflictException("Ya existe un cliente registrado con el RUT " + rutNormalizado);
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(normalizarTexto(request.getNombre()));
        cliente.setRut(rutNormalizado);
        cliente.setEmail(normalizarEmail(request.getEmail()));
        cliente.setTelefono(normalizarTexto(request.getTelefono()));
        cliente.setDireccion(normalizarTexto(request.getDireccion()));
        cliente.setComuna(normalizarTexto(request.getComuna()));
        cliente.setRegion(normalizarTexto(request.getRegion()));
        cliente.setFechaRegistro(LocalDate.now());
        cliente.setEstado(ESTADO_ACTIVO);

        Cliente guardado = clienteRepository.save(cliente);
        cacheClientes.put(guardado.getId(), guardado);
        return convertirAResponse(guardado);
    }

    public List<ClienteResponse> listarClientes() {
        return clienteRepository.findByEstado(ESTADO_ACTIVO)
                .stream()
                .peek(cliente -> cacheClientes.put(cliente.getId(), cliente))
                .map(this::convertirAResponse)
                .toList();
    }

    public ClienteResponse obtenerClientePorId(Long id) {
        Cliente cliente = cacheClientes.get(id);
        if (cliente == null) {
            cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID " + id));
            cacheClientes.put(cliente.getId(), cliente);
        }

        validarClienteActivo(cliente);
        return convertirAResponse(cliente);
    }

    public ClienteResponse obtenerClientePorRut(String rut) {
        String rutNormalizado = normalizarTexto(rut);
        Cliente cliente = clienteRepository.findByRut(rutNormalizado)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con RUT " + rutNormalizado));

        validarClienteActivo(cliente);
        cacheClientes.put(cliente.getId(), cliente);
        return convertirAResponse(cliente);
    }

    public ClienteResponse actualizarCliente(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID " + id));

        validarClienteActivo(cliente);

        String rutNormalizado = normalizarTexto(request.getRut());
        if (!cliente.getRut().equals(rutNormalizado) && clienteRepository.existsByRut(rutNormalizado)) {
            throw new ConflictException("Ya existe otro cliente registrado con el RUT " + rutNormalizado);
        }

        cliente.setNombre(normalizarTexto(request.getNombre()));
        cliente.setRut(rutNormalizado);
        cliente.setEmail(normalizarEmail(request.getEmail()));
        cliente.setTelefono(normalizarTexto(request.getTelefono()));
        cliente.setDireccion(normalizarTexto(request.getDireccion()));
        cliente.setComuna(normalizarTexto(request.getComuna()));
        cliente.setRegion(normalizarTexto(request.getRegion()));

        Cliente actualizado = clienteRepository.save(cliente);
        cacheClientes.put(actualizado.getId(), actualizado);
        return convertirAResponse(actualizado);
    }

    public void eliminarClienteLogico(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID " + id));

        cliente.setEstado(ESTADO_INACTIVO);
        Cliente actualizado = clienteRepository.save(cliente);
        cacheClientes.put(actualizado.getId(), actualizado);
    }

    private void validarClienteActivo(Cliente cliente) {
        if (ESTADO_INACTIVO.equalsIgnoreCase(cliente.getEstado())) {
            throw new ResourceNotFoundException("Cliente no encontrado o inactivo");
        }
    }

    private ClienteResponse convertirAResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getRut(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getComuna(),
                cliente.getRegion(),
                cliente.getFechaRegistro(),
                cliente.getEstado()
        );
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return null;
        }
        return texto.trim();
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}
