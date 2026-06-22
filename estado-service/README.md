# estado-service

## Proposito

Microservicio encargado de registrar cambios de estado de pedidos y consultar su historial.

## Puerto

`8086`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/estados` | Registra cambio de estado |
| GET | `/api/estados/pedido/{numeroPedido}` | Lista cambios de estado de un pedido |
| GET | `/api/estados/pedido/{numeroPedido}/ultimo` | Obtiene ultimo estado registrado |

## Dependencias

- No consume otros microservicios.
- Es consumido por `pedido-service`.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/estado_db;DB_CLOSE_DELAY=-1`.
- Incluido en la demo Docker actual con `SPRING_PROFILES_ACTIVE=h2`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

Swagger no implementado actualmente.

## Tests

Sin pruebas unitarias propias actualmente.

## Ejecucion local

```powershell
cd estado-service
.\mvnw spring-boot:run
```
