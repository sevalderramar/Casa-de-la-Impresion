# api-gateway

## Proposito

Servicio de entrada central que enruta solicitudes HTTP hacia los microservicios del sistema.

## Puerto

`8080`

## Rutas principales

| Ruta | Destino configurable |
|---|---|
| `/api/clientes/**` | `CLIENTE_SERVICE_URL`, default `http://localhost:8082` |
| `/api/productos/**` | `PRODUCTO_SERVICE_URL`, default `http://localhost:8083` |
| `/api/pedidos/**` | `PEDIDO_SERVICE_URL`, default `http://localhost:8081` |
| `/api/estados/**` | `ESTADO_SERVICE_URL`, default `http://localhost:8086` |

Tambien existen rutas configuradas hacia otros servicios con valores locales fijos en `application.yml`.

## Variables y perfil

- `CLIENTE_SERVICE_URL`: opcional, default `http://localhost:8082`.
- `PRODUCTO_SERVICE_URL`: opcional, default `http://localhost:8083`.
- `PEDIDO_SERVICE_URL`: opcional, default `http://localhost:8081`.
- `ESTADO_SERVICE_URL`: opcional, default `http://localhost:8086`.
- En la demo Docker actual recibe `SPRING_PROFILES_ACTIVE=h2` y URLs internas de Docker.

## Docker

Participa en la demo Docker actual junto con `cliente-service`, `producto-service`, `pedido-service` y `estado-service`.

## Swagger

Swagger no implementado actualmente en api-gateway.

## Tests

Sin pruebas unitarias propias actualmente.

## Ejecucion local

```powershell
cd api-gateway
.\mvnw spring-boot:run
```
