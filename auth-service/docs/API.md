# API - auth-service

Base URL local: `http://localhost:8090`

## Convenciones
- API de autenticacion bajo `/api/auth`.
- Endpoints de usuarios bajo `/api/auth/usuarios`.
- Header JWT para endpoints protegidos:

```http
Authorization: Bearer <TOKEN_JWT>
```

## Endpoints publicos

### GET `/api/auth/ping`
Healthcheck simple.

**Response 200**
```text
auth-service OK
```

### POST `/api/auth/login`
Autentica usuario y retorna JWT.

**Request**
```json
{
  "email": "admin@empresa.com",
  "password": "pass123"
}
```

**Response 200**
```json
{
  "mensaje": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tipo": "Bearer",
    "email": "admin@empresa.com",
    "rol": "ADMIN",
    "expiracion": 1760000000000
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

## Endpoints protegidos

### POST `/api/auth/logout`
Cierre de sesion logico (stateless).

### GET `/api/auth/usuarios`
Lista usuarios. Requiere rol `ADMIN`.

### POST `/api/auth/usuarios`
Crea usuario. Requiere rol `ADMIN`.

**Request**
```json
{
  "nombre": "Pedro Soto",
  "email": "pedro@empresa.com",
  "password": "mypassword",
  "rol": "COMERCIAL"
}
```

### PUT `/api/auth/usuarios/{id}`
Actualiza usuario. Requiere rol `ADMIN`.

## Errores comunes
- `400`: validacion de DTO.
- `401`: token ausente/invalido o usuario inactivo.
- `403`: rol insuficiente.
- `404`: recurso no encontrado.
- `409`: conflicto de negocio.
- `500`: error interno.
