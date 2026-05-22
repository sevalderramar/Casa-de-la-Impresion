# auth-service

## 1. Nombre del microservicio
`auth-service`

## 2. Descripcion breve
Microservicio de autenticacion y gestion de usuarios para la plataforma Casa de la Impresion.

## 3. Responsabilidad dentro del sistema
- Validar credenciales de usuario.
- Generar tokens JWT.
- Gestionar usuarios del dominio de autenticacion.

## 4. Puerto
`8090`

## 5. Tecnologias usadas
- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Spring Security + JWT
- H2
- Maven
- OpenFeign

## 6. Profiles disponibles
- `h2`
- `prod`

## 7. Base de datos H2
- URL (h2): `jdbc:h2:mem:auth_db`
- Driver: `org.h2.Driver`

## 8. H2 Console
- Habilitada en profile `h2`
- URL: `http://localhost:8090/h2-console`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto para firma/validacion JWT |
| `JWT_EXPIRATION_MS` | No | Expiracion del token en ms (default `86400000`) |
| `DB_URL` | Solo prod | URL de base de datos en produccion |
| `DB_USERNAME` | Solo prod | Usuario de base de datos en produccion |
| `DB_PASSWORD` | Solo prod | Password de base de datos en produccion |
| `PORT` | No | Puerto en produccion (default `8090`) |

## 10. Seguridad JWT
- Endpoint publico: `POST /api/auth/login`
- Endpoint publico: `GET /api/auth/ping`
- Endpoints de negocio protegidos con Bearer Token.
- Header esperado:

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/auth/login` | Login y entrega de token JWT |
| POST | `/api/auth/logout` | Cierre de sesion logico |
| GET | `/api/auth/ping` | Healthcheck simple |
| GET | `/api/auth/usuarios` | Lista usuarios (ADMIN) |
| POST | `/api/auth/usuarios` | Crea usuario (ADMIN) |
| PUT | `/api/auth/usuarios/{id}` | Actualiza usuario (ADMIN) |

## 12. Ejemplos de request JSON
```json
{
  "email": "admin@casaimpresion.cl",
  "password": "123456"
}
```

## 13. Ejemplos de response JSON
```json
{
  "mensaje": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tipo": "Bearer",
    "email": "admin@casaimpresion.cl",
    "rol": "ADMIN",
    "expiracion": 1760000000000
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

## 14. Comunicacion Feign si aplica
Este servicio incluye OpenFeign en dependencias, pero su rol principal es emision/validacion de JWT y gestion de usuarios.

## 15. Manejo global de errores Lesson 18
`GlobalExceptionHandler` centraliza:
- `ResourceNotFoundException` -> 404
- `ConflictException` -> 409
- `MethodArgumentNotValidException` -> 400
- `Exception` -> 500

Formato estandar:

```json
{
  "mensaje": "Descripcion del error",
  "status": 400
}
```

## 16. Como compilar
```powershell
.\mvnw clean compile
```

## 17. Como ejecutar
```powershell
$env:JWT_SECRET="BASE64_SECRET"
.\mvnw spring-boot:run
```

## 18. Estructura de carpetas
```text
auth-service/
  src/main/java/cl/duocuc/authservice/
    config/
    controller/
    dto/
    entity/
    exception/
    repository/
    service/
  src/main/resources/
    application.properties
    application-h2.properties
    application-prod.properties
```

## 19. Estado actual del servicio
- Compila correctamente con `Java 21` y `Spring Boot 4.0.5`.
- Seguridad JWT activa.
- Profile por defecto: `h2`.

