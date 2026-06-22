# producto-service

## Proposito

Microservicio encargado de administrar el catalogo de productos que se usan como items de los pedidos.

## Puerto

`8083`

## Endpoints principales

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/productos` | Crea un producto |
| GET | `/api/productos` | Lista productos |
| GET | `/api/productos/{id}` | Obtiene producto por ID |
| GET | `/api/productos/nombre/{nombre}` | Busca producto por nombre |
| GET | `/api/productos/categoria/{categoria}` | Lista productos por categoria |
| PUT | `/api/productos/{id}` | Actualiza producto |
| DELETE | `/api/productos/{id}` | Elimina producto |

## Dependencias

- No consume otros microservicios.
- Es consumido por `pedido-service`.

## Variables y perfil

- `JWT_SECRET`: requerido por la configuracion JWT.
- `JWT_EXPIRATION_MS`: opcional, default `86400000`.
- Perfil local: `h2`.
- Base H2 real: `jdbc:h2:file:./data/producto_db;DB_CLOSE_DELAY=-1`.
- Incluido en la demo Docker actual con `SPRING_PROFILES_ACTIVE=h2`.

La configuracion actual de seguridad esta en modo demo/local: aunque existen filtros JWT, `SecurityConfig` termina permitiendo las solicitudes con `anyRequest().permitAll()`.

## Swagger

- Swagger UI: `http://localhost:8083/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8083/v3/api-docs`

## Tests

Tiene pruebas unitarias JUnit 5 + Mockito en `src/test/java/cl/duocuc/productoservice/service/ProductoServiceTest.java`.

## Ejecucion local

```powershell
cd producto-service
.\mvnw spring-boot:run
```
