# despacho-service

## Proposito

Microservicio encargado de registrar, consultar y actualizar despachos asociados a pedidos.

## Puerto

`8084`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/despachos` | Crea un despacho |
| GET | `/api/despachos` | Lista despachos; admite filtro opcional `tipo` |
| GET | `/api/despachos/{numeroPedido}` | Obtiene despacho por numero de pedido |
| PUT | `/api/despachos/{id}` | Actualiza despacho |

## Dependencias

- Consume `pedido-service` mediante Feign: `PEDIDO_SERVICE_URL`.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- `PEDIDO_SERVICE_URL`: default `http://localhost:8081`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/despacho_db;DB_CLOSE_DELAY=-1`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

- Swagger UI: `http://localhost:8084/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8084/v3/api-docs`

## Tests

48 tests pasando. Cobertura de lineas sobre 80% con JaCoCo.

## Ejecucion local

```powershell
cd despacho-service
.\mvnw spring-boot:run
```
