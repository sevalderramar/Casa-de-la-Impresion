# Arquitectura - auth-service

## Vision
`auth-service` implementa autenticacion stateless con JWT y administracion de usuarios.

## Capas
- `controller`: expone login, ping, logout y gestion de usuarios.
- `service`: reglas de autenticacion y ciclo de vida de usuarios.
- `repository`: acceso JPA a `Usuario`.
- `config`: `SecurityConfig`, `JwtUtil`, `JwtAuthFilter`, `CustomUserDetailsService`.
- `exception`: manejo global de errores (Lesson 18).

## Flujo de autenticacion
1. Cliente llama `POST /api/auth/login` con credenciales.
2. `AuthService` valida credenciales contra BD.
3. `JwtUtil` genera token firmado.
4. Cliente reusa token en `Authorization: Bearer ...`.
5. `JwtAuthFilter` valida token e inyecta autenticacion al contexto de seguridad.

## Control de acceso
- Publico: `POST /api/auth/login`, `GET /api/auth/ping`, `/h2-console/**`, `/actuator/health`.
- Protegido: resto de endpoints.
- Restringido a `ADMIN`: endpoints de `/api/auth/usuarios`.

## Integracion
Este servicio emite tokens y no depende de otros microservicios para validar sesiones.
