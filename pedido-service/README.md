# pedido-service

## Proposito

Microservicio encargado de crear y consultar pedidos, calcular montos, validar clientes/productos y registrar cambios de estado.

## Puerto

`8081`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/pedidos` | Crea un pedido |
| GET | `/api/pedidos` | Lista pedidos |
| GET | `/api/pedidos/{numeroPedido}` | Obtiene pedido por numero |
| GET | `/api/pedidos/numero/{numeroPedido}` | Obtiene pedido por numero usando ruta alternativa |
| GET | `/api/pedidos/cliente/{clienteId}` | Lista pedidos por cliente |
| POST | `/api/pedidos/{numeroPedido}/estado` | Cambia estado del pedido |
| GET | `/api/pedidos/{numeroPedido}/historial` | Consulta historial de estados |
| DELETE | `/api/pedidos/{numeroPedido}` | Elimina pedido |

## Dependencias

- Consume `cliente-service` mediante Feign: `CLIENTE_SERVICE_URL`.
- Consume `producto-service` mediante Feign: `PRODUCTO_SERVICE_URL`.
- Consume `estado-service` mediante Feign: `ESTADO_SERVICE_URL`.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- `CLIENTE_SERVICE_URL`: default `http://localhost:8082`.
- `PRODUCTO_SERVICE_URL`: default `http://localhost:8083`.
- `ESTADO_SERVICE_URL`: default `http://localhost:8086`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/pedido_db;DB_CLOSE_DELAY=-1`.
- Incluido en la demo Docker actual con `SPRING_PROFILES_ACTIVE=h2`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## Tests

Tiene pruebas unitarias JUnit 5 + Mockito en `src/test/java/cl/duocuc/pedidoservice/service/PedidoServiceTest.java`.

## Ejecucion local

```powershell
cd pedido-service
.\mvnw spring-boot:run
```
