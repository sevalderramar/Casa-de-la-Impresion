# fabricacion-service

## Proposito

Microservicio encargado de crear ordenes de fabricacion asociadas a pedidos y actualizar su estado de fabricacion.

## Puerto

`8085`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/fabricacion/ping` | Healthcheck simple |
| POST | `/api/fabricacion` | Crea orden de fabricacion |
| GET | `/api/fabricacion/{id}` | Obtiene orden de fabricacion por ID |
| PATCH | `/api/fabricacion/{id}/estado` | Actualiza estado de fabricacion |

## Dependencias

- Consume `pedido-service` mediante Feign: `PEDIDO_SERVICE_URL`.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- `PEDIDO_SERVICE_URL`: default `http://localhost:8081`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/fabricacion_db;DB_CLOSE_DELAY=-1`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

- Swagger UI: `http://localhost:8085/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8085/v3/api-docs`

## Tests

76 tests pasando. Cobertura de lineas sobre 80% con JaCoCo.

## Ejecucion local

```powershell
cd fabricacion-service
.\mvnw spring-boot:run
```
