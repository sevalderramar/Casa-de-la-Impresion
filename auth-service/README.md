# auth-service

## Proposito

Microservicio encargado de autenticar usuarios, emitir tokens JWT y administrar usuarios del dominio de autenticacion.

## Puerto

`8090`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/auth/login` | Valida credenciales y entrega token JWT |
| POST | `/api/auth/logout` | Logout logico del cliente |
| GET | `/api/auth/ping` | Healthcheck simple |
| GET | `/api/auth/usuarios` | Lista usuarios |
| POST | `/api/auth/usuarios` | Crea usuario |
| PUT | `/api/auth/usuarios/{id}` | Actualiza usuario |

## Dependencias

- No consume otros microservicios.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/auth_db;DB_CLOSE_DELAY=-1`.

JWT esta implementado. En modo demo/local se permiten explicitamente Swagger, H2, healthcheck y login para facilitar pruebas; los endpoints de usuarios mantienen reglas de rol/autenticacion en `SecurityConfig`.

Credencial H2 de ejemplo cargada por `DataInitializer`: `admin@empresa.com` / `pass123`.

## Swagger

- Swagger UI: `http://localhost:8090/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8090/v3/api-docs`

## Tests

50 tests pasando. Cobertura de lineas sobre 80% con JaCoCo.

## Ejecucion local

```powershell
cd auth-service
.\mvnw spring-boot:run
```
