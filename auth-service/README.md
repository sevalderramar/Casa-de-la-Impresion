# auth-service

## Proposito

Microservicio encargado de autenticar usuarios, emitir tokens JWT y administrar usuarios del dominio de autenticacion.

## Puerto

`8090`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/auth/login` | Valida credenciales y entrega token JWT |
| POST | `/api/auth/logout` | Logout logico del cliente |
| GET | `/api/auth/ping` | Healthcheck simple |
| GET | `/api/auth/usuarios` | Lista usuarios |
| POST | `/api/auth/usuarios` | Crea usuario |
| PUT | `/api/auth/usuarios/{id}` | Actualiza usuario |

## Dependencias

- No consume otros microservicios.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/auth_db;DB_CLOSE_DELAY=-1`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

Swagger no implementado actualmente.

## Tests

Sin pruebas unitarias propias actualmente.

## Ejecucion local

```powershell
cd auth-service
.\mvnw spring-boot:run
```
