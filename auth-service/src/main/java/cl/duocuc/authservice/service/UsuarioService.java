package cl.duocuc.authservice.service;

import cl.duocuc.authservice.dto.UsuarioRequestDTO;
import cl.duocuc.authservice.dto.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponseDTO> listar();
    UsuarioResponseDTO crear(UsuarioRequestDTO request);
    UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request);
}
