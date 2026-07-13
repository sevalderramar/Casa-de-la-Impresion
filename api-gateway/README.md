# api-gateway

## Proposito

Servicio de entrada central que enruta solicitudes HTTP hacia los microservicios del sistema.

## Puerto

`8080`

## Rutas principales

| Ruta | Destino local/configuracion |
|---|---|
| `/api/auth/**` | `http://localhost:8090` |
| `/api/pedidos/**` | `PEDIDO_SERVICE_URL`, default `http://localhost:8081` |
| `/api/clientes/**` | `CLIENTE_SERVICE_URL`, default `http://localhost:8082` |
| `/api/productos/**` | `PRODUCTO_SERVICE_URL`, default `http://localhost:8083` |
| `/api/despachos/**` | `http://localhost:8084` |
| `/api/fabricacion/**` | `http://localhost:8085` |
| `/api/estados/**` | `ESTADO_SERVICE_URL`, default `http://localhost:8086` |
| `/api/metricas/**` | `http://localhost:8087` |
| `/api/transportistas/**` | `http://localhost:8088` |
| `/api/logs/**` | `http://localhost:8089` |

El discovery actual es estatico: las rutas se resuelven desde `application.yml` y variables de entorno cuando estan configuradas. No hay Eureka Server real.

## Variables y perfil

- `CLIENTE_SERVICE_URL`: opcional, default `http://localhost:8082`.
- `PRODUCTO_SERVICE_URL`: opcional, default `http://localhost:8083`.
- `PEDIDO_SERVICE_URL`: opcional, default `http://localhost:8081`.
- `ESTADO_SERVICE_URL`: opcional, default `http://localhost:8086`.
- En la demo Docker actual recibe `SPRING_PROFILES_ACTIVE=h2` y URLs internas de Docker.

## Docker

Participa en la demo Docker actual junto con `cliente-service`, `producto-service`, `pedido-service` y `estado-service`.

## Swagger

El Gateway no tiene Swagger propio. La documentacion OpenAPI se consulta directamente en cada microservicio, por ejemplo `http://localhost:8081/swagger-ui/index.html` para `pedido-service`.

## Tests

No tiene pruebas unitarias propias actualmente. El Gateway compila y su responsabilidad principal es enrutar solicitudes declarativamente.

## Ejecucion local

```powershell
cd api-gateway
.\mvnw spring-boot:run
```
