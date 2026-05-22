package cl.duocuc.transportistaservice.service;

import cl.duocuc.transportistaservice.dto.TransportistaRequestDTO;
import cl.duocuc.transportistaservice.dto.TransportistaResponseDTO;
import cl.duocuc.transportistaservice.dto.TransportistaUpdateDTO;

import java.util.List;

public interface TransportistaService {
    TransportistaResponseDTO crearTransportista(TransportistaRequestDTO request);
    List<TransportistaResponseDTO> listarActivos();
    TransportistaResponseDTO obtenerPorId(Long id);
    TransportistaResponseDTO actualizarTransportista(Long id, TransportistaUpdateDTO request);
}
