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

## 15. Manejo global de errores

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

## 16. Funcionamiento general del servicio

`auth-service` implementa un flujo de autenticación stateless basado en JWT (JSON Web Tokens):

1. **Login**: El usuario proporciona correo y contraseña en `POST /api/auth/login`.
2. **Generación de Token**: El servicio valida las credenciales y genera un token JWT firmado.
3. **Reutilización**: El cliente incluye el token en el header `Authorization: Bearer <TOKEN>` para acceder a endpoints protegidos.
4. **Validación**: Un filtro JWT (`JwtAuthFilter`) valida cada request y inyecta la autenticación en el contexto de seguridad.
5. **Gestión de Usuarios**: Solo usuarios con rol `ADMIN` pueden crear/actualizar usuarios.

## 17. Como compilar desde terminal
```powershell
cd .\auth-service
.\mvnw clean compile
```

## 18. Como ejecutar desde terminal
```powershell
cd .\auth-service
$env:JWT_SECRET="<TU_JWT_SECRET_BASE64>"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 19. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → selecciona la carpeta `auth-service`
2. **Configurar la variable de entorno JWT_SECRET**: 
   - Edit Configurations (esquina superior derecha)
   - Create new → Spring Boot
   - Name: `auth-service`
   - Main class: `cl.duocuc.authservice.AuthServiceApplication`
   - Enviroment variables: `JWT_SECRET=<TU_JWT_SECRET_BASE64>`
   - Active profiles: `h2`
3. **Ejecutar**: Presiona el botón "Run" (▶) o Shift+F10
4. **Verificar**: Abre navegador en `http://localhost:8090/h2-console`
   - Usuario: `sa`
   - Contraseña: (dejar vacío)

## 20. Testear endpoints con Postman

### 1. Login (Público)
```http
POST http://localhost:8090/api/auth/login
Content-Type: application/json

{
  "email": "admin@casaimpresion.cl",
  "password": "123456"
}
```
**Respuesta esperada (201)**:
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

### 2. Ping (Healthcheck - Público)
```http
GET http://localhost:8090/api/auth/ping
```

### 3. Listar Usuarios (Protegido - ADMIN)
```http
GET http://localhost:8090/api/auth/usuarios
Authorization: Bearer <TOKEN_JWT_DEL_LOGIN>
```

## 21. Estructura de carpetas
```
auth-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/authservice/
│       │   ├── config/         (JwtUtil, SecurityConfig, JwtAuthFilter)
│       │   ├── controller/     (AuthController, UsuarioController)
│       │   ├── dto/            (LoginRequestDTO, UsuarioResponseDTO, etc)
│       │   ├── entity/         (Usuario)
│       │   ├── exception/      (ResourceNotFoundException, ConflictException)
│       │   ├── repository/     (UsuarioRepository)
│       │   ├── service/        (AuthService, UsuarioService)
│       │   └── AuthServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── auth_db.mv.db
```

## 22. Estado actual del servicio
- ✅ Compila correctamente con `Java 21` y `Spring Boot 4.0.5`.
- ✅ Seguridad JWT activa y funcional.
- ✅ Profile por defecto: `h2`.
- ✅ H2 Console disponible en `http://localhost:8090/h2-console`.
- ✅ Todos los endpoints documentados y probados.

