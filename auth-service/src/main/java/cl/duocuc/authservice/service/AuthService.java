package cl.duocuc.authservice.service;

import cl.duocuc.authservice.dto.LoginRequestDTO;
import cl.duocuc.authservice.dto.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
}
