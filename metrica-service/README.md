# metrica-service

## Proposito

Microservicio encargado de exponer metricas de clientes, productos y ventas a partir de datos consultados a otros servicios.

## Puerto

`8087`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/metricas/clientes/{id}` | Obtiene metricas de un cliente |
| GET | `/api/metricas/clientes/ranking` | Obtiene ranking de clientes |
| GET | `/api/metricas/productos/top` | Obtiene top de productos |
| GET | `/api/metricas/ventas` | Obtiene resumen de ventas |
| GET | `/api/metricas/ping` | Healthcheck simple |

## Dependencias

- Consume `cliente-service` mediante Feign: `CLIENTE_SERVICE_URL`.
- Consume `pedido-service` mediante Feign: `PEDIDO_SERVICE_URL`.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- `CLIENTE_SERVICE_URL`: default `http://localhost:8082`.
- `PEDIDO_SERVICE_URL`: default `http://localhost:8081`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/metrica_db;DB_CLOSE_DELAY=-1`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

- Swagger UI: `http://localhost:8087/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8087/v3/api-docs`

## Tests

Sin pruebas unitarias propias actualmente.

## Ejecucion local

```powershell
cd metrica-service
.\mvnw spring-boot:run
```
