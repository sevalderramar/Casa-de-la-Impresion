# despacho-service

## 1) Nombre del microservicio
**despacho-service**

## 2) Objetivo del microservicio
Registrar y gestionar la información de despacho de pedidos, validando que el pedido exista antes de crear el despacho y evitando duplicados por `numeroPedido`.

## 3) Stack y configuración
- **Java 25**
- **Spring Boot 4.0.5**
- **Maven**
- **Spring Web**
- **Spring Data JPA**
- **Validation**
- **Lombok**
- **OpenFeign**
- **H2** en memoria
- **Flyway** para versionamiento de base de datos
- **Spring Boot DevTools**
- **Spring Configuration Processor**
- **spring-boot-h2console**

## 4) Package base
`cl.duocuc.despachoservice`

## 5) Puerto
Configurado en `src/main/resources/application.properties`:

```properties
server.port=8084
```

## 6) Base de datos H2
Configuración activa en memoria en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:despacho_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

- Motor: **H2 en memoria** (`despacho_db`)
- JPA: solo valida el esquema con `ddl-auto=validate`
- Flyway: crea y versiona las tablas al iniciar la aplicación
- Consola H2: `http://localhost:8084/h2-console`

## 7) Flujo de Flyway implementado
Al iniciar la aplicación:
1. Spring Boot lee `application.properties`.
2. Flyway busca migraciones en `classpath:db/migration`.
3. Ejecuta `V1__create_despachos_table.sql`.
4. Crea la tabla `despachos` con la misma estructura de la entidad JPA.
5. Crea automáticamente la tabla `flyway_schema_history`.
6. Luego Hibernate valida el esquema con `ddl-auto=validate`.

## 8) Migración creada
Archivo:

`src/main/resources/db/migration/V1__create_despachos_table.sql`

La tabla `despachos` contiene:
- `id`
- `numero_pedido`
- `tipo_despacho`
- `transportista`
- `fecha_despacho`
- `tracking_code`

Además, incluye restricción única sobre `numero_pedido`.

## 9) Entidad principal
### `Despacho`
Campos:
- `id` `Long`
- `numeroPedido` `Long`
- `tipoDespacho` `String`
- `transportista` `String`
- `fechaDespacho` `LocalDateTime`
- `trackingCode` `String`

## 10) DTOs
- `DespachoRequest`
- `DespachoResponse`

## 11) Reglas de negocio
- Antes de crear un despacho, se valida que el pedido exista vía Feign.
- Si el pedido no existe, se responde **404 Not Found**.
- Si el pedido ya tiene despacho, se responde **409 Conflict**.
- No existe relación JPA directa con `Pedido`.
- Solo se guarda `numeroPedido`.
- Se usan validaciones `jakarta.validation` en el request.

## 12) FeignClient
Se creó `PedidoFeignClient` para consultar:

`GET http://localhost:8081/api/pedidos/{numeroPedido}`

> Nota: la URL base puede ajustarse en `services.pedido.url` dentro de `application.properties`.

## 13) Endpoints
Base URL: `http://localhost:8084/api/despachos`

- `POST /api/despachos` -> crea un despacho.
- `GET /api/despachos` -> lista todos los despachos.
- `GET /api/despachos?tipo=` -> filtra por `tipoDespacho`.
- `GET /api/despachos/{numeroPedido}` -> obtiene un despacho por número de pedido.
- `PUT /api/despachos/{id}` -> actualiza un despacho.

## 14) Ejemplos para Postman
### Crear despacho
**Request**
- Método: `POST`
- URL: `http://localhost:8084/api/despachos`
- Header: `Content-Type: application/json`

```json
{
  "numeroPedido": 1,
  "tipoDespacho": "RM",
  "transportista": "Starken",
  "trackingCode": "TRK-001"
}
```

**Response esperado (201 Created)**

```json
{
  "id": 1,
  "numeroPedido": 1,
  "tipoDespacho": "RM",
  "transportista": "Starken",
  "fechaDespacho": "2026-05-13T10:30:00",
  "trackingCode": "TRK-001"
}
```

### Listar despachos
- Método: `GET`
- URL: `http://localhost:8084/api/despachos`

### Filtrar por tipo
- Método: `GET`
- URL: `http://localhost:8084/api/despachos?tipo=RM`

### Obtener por número de pedido
- Método: `GET`
- URL: `http://localhost:8084/api/despachos/1`

### Actualizar despacho
- Método: `PUT`
- URL: `http://localhost:8084/api/despachos/1`
- Header: `Content-Type: application/json`

```json
{
  "numeroPedido": 1,
  "tipoDespacho": "REGION",
  "transportista": "Chilexpress",
  "trackingCode": "TRK-002"
}
```

## 15) Errores esperados
- **400 Bad Request**: datos inválidos en `DespachoRequest` o tipo no permitido.
- **404 Not Found**: pedido inexistente o despacho inexistente.
- **409 Conflict**: intento de crear o reasignar un despacho para un pedido que ya tiene uno.
- **500 Internal Server Error**: error no controlado.

## 16) Cómo ejecutar
Desde la carpeta `despacho-service`:

```bash
mvn clean compile
mvn spring-boot:run
```

## 17) Verificación en H2 Console
1. Iniciar el servicio.
2. Abrir `http://localhost:8084/h2-console`.
3. Usar:
   - JDBC URL: `jdbc:h2:mem:despacho_db`
   - User Name: `sa`
   - Password: vacío
4. Consultar:

```sql
SELECT * FROM despachos;
SELECT * FROM flyway_schema_history;
```
