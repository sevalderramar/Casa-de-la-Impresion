# log-service

## Proposito

Microservicio encargado de registrar y consultar eventos operacionales del ecosistema.

## Puerto

`8089`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/logs` | Registra un log operacional |
| GET | `/api/logs` | Consulta logs con filtros opcionales `servicio` y `desde` |
| GET | `/api/logs/ping` | Healthcheck simple |

## Dependencias

- No consume otros microservicios.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/log_db;DB_CLOSE_DELAY=-1`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

Swagger no implementado actualmente.

## Tests

Sin pruebas unitarias propias actualmente.

## Ejecucion local

```powershell
cd log-service
.\mvnw spring-boot:run
```
