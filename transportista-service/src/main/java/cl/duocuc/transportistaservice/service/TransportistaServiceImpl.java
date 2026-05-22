package cl.duocuc.transportistaservice.service;

import cl.duocuc.transportistaservice.dto.TransportistaRequestDTO;
import cl.duocuc.transportistaservice.dto.TransportistaResponseDTO;
import cl.duocuc.transportistaservice.dto.TransportistaUpdateDTO;
import cl.duocuc.transportistaservice.exception.ConflictException;
import cl.duocuc.transportistaservice.exception.ResourceNotFoundException;
import cl.duocuc.transportistaservice.model.Transportista;
import cl.duocuc.transportistaservice.repository.TransportistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
public class TransportistaServiceImpl implements TransportistaService {

    private final TransportistaRepository transportistaRepository;

    @Override
    @Transactional
    public TransportistaResponseDTO crearTransportista(TransportistaRequestDTO request) {
        if (transportistaRepository.existsByCodigoInterno(request.getCodigoInterno())) {
            throw new ConflictException("El código interno ya existe");
        }

        Transportista t = new Transportista();
        t.setNombre(request.getNombre());
        t.setCodigoInterno(request.getCodigoInterno());
        t.setContacto(request.getContacto());
        t.setRegionesCobertura(request.getRegionesCobertura());
        t.setActivo(true);

        return toDTO(transportistaRepository.save(t));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransportistaResponseDTO> listarActivos() {
        return transportistaRepository.findByActivoTrue().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TransportistaResponseDTO obtenerPorId(Long id) {
        Transportista t = transportistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado"));

        if (!t.isActivo()) {
            throw new ResourceNotFoundException("Transportista inactivo");
        }
        
        return toDTO(t);
    }

    @Override
    @Transactional
    public TransportistaResponseDTO actualizarTransportista(Long id, TransportistaUpdateDTO request) {
        Transportista t = transportistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado"));

        if (request.getNombre() != null && !request.getNombre().trim().isEmpty()) {
            t.setNombre(request.getNombre());
        }
        if (request.getContacto() != null) {
            t.setContacto(request.getContacto());
        }
        if (request.getRegionesCobertura() != null) {
            t.setRegionesCobertura(request.getRegionesCobertura());
        }
        if (request.getActivo() != null) {
            t.setActivo(request.getActivo());
        }

        return toDTO(transportistaRepository.save(t));
    }

    private TransportistaResponseDTO toDTO(Transportista t) {
        TransportistaResponseDTO dto = new TransportistaResponseDTO();
        dto.setId(t.getId());
        dto.setNombre(t.getNombre());
        dto.setCodigoInterno(t.getCodigoInterno());
        dto.setContacto(t.getContacto());
        dto.setRegionesCobertura(t.getRegionesCobertura());
        dto.setActivo(t.isActivo());
        return dto;
    }
}

