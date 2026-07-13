# transportista-service

## Proposito

Microservicio encargado de registrar, listar, consultar y actualizar transportistas.

## Puerto

`8088`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/transportistas` | Crea transportista |
| GET | `/api/transportistas` | Lista transportistas |
| GET | `/api/transportistas/{id}` | Obtiene transportista por ID |
| PUT | `/api/transportistas/{id}` | Actualiza transportista |
| GET | `/api/transportistas/ping` | Healthcheck simple |

## Dependencias

- No consume otros microservicios.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/transportista_db;DB_CLOSE_DELAY=-1`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

- Swagger UI: `http://localhost:8088/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8088/v3/api-docs`

## Tests

40 tests pasando. Cobertura de lineas sobre 80% con JaCoCo.

## Ejecucion local

```powershell
cd transportista-service
.\mvnw spring-boot:run
```
