# cliente-service

## Proposito

Microservicio encargado de registrar, consultar, actualizar y eliminar clientes usados por el flujo de pedidos.

## Puerto

`8082`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/clientes` | Crea un cliente |
| GET | `/api/clientes` | Lista clientes |
| GET | `/api/clientes/{id}` | Obtiene cliente por ID |
| GET | `/api/clientes/rut/{rut}` | Obtiene cliente por RUT |
| PUT | `/api/clientes/{id}` | Actualiza cliente |
| DELETE | `/api/clientes/{id}` | Elimina cliente |

## Dependencias

- No consume otros microservicios.
- Es consumido por `pedido-service` y `metrica-service`.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/cliente_db;DB_CLOSE_DELAY=-1`.
- Incluido en la demo Docker actual con `SPRING_PROFILES_ACTIVE=h2`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

- Swagger UI: `http://localhost:8082/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8082/v3/api-docs`

## Tests

Tiene pruebas unitarias JUnit 5 + Mockito en `src/test/java/cl/duocuc/clienteservice/service/ClienteServiceTest.java`.

## Ejecucion local

```powershell
cd cliente-service
.\mvnw spring-boot:run
```
