# cliente-service

## 1) Nombre del microservicio
**cliente-service**

## 2) Objetivo del microservicio
Gestionar el ciclo de vida de clientes del sistema: creación, consulta, actualización y eliminación (CRUD), garantizando validación de datos y unicidad de RUT.

## 3) Problema que resuelve dentro del sistema
Este microservicio desacopla la gestión de clientes del resto de dominios (pedidos, productos, estados), centralizando:
- Registro único de clientes.
- Consulta por ID y por RUT.
- Persistencia y trazabilidad básica (`fechaRegistro`).
- Reglas de validación y control de conflictos de datos.

## 4) Puerto donde corre
Configurado en `src/main/resources/application.properties`:

```properties
server.port=8081
```

## 5) Base de datos H2 usada
Configuración activa en memoria:

```properties
spring.datasource.url=jdbc:h2:mem:cliente_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

- Motor: **H2 en memoria** (`cliente_db`)
- Persistencia: temporal durante la ejecución del proceso
- Consola web: `http://localhost:8081/h2-console`

## 6) Dependencias principales
Declaradas en `pom.xml`:
- **Spring Web** (vía `spring-boot-starter-webmvc`)
- **Spring Data JPA**
- **H2 Database**
- **MySQL Driver** (`mysql-connector-j`, runtime)
- **Validation** (`spring-boot-starter-validation`)
- **Lombok**
- **OpenFeign** (`spring-cloud-starter-openfeign`)

## 7) Arquitectura interna
Package base del microservicio:

`cl.duocuc.clienteservice`

Estructura actual:
- `controller/`: expone endpoints REST.
- `dto/`: contratos de entrada/salida para API.
- `model/`: entidades JPA.
- `repository/`: acceso a datos con Spring Data.
- `service/`: reglas de negocio y orquestación.
- `client/` (si existe): **no existe actualmente** en este servicio.
- `common.exception`: en este microservicio se utiliza `exception/` local (`cl.duocuc.clienteservice.exception`) como equivalente desacoplado.

## 8) Clases principales y responsabilidad
- `ClienteServiceApplication`: punto de entrada del microservicio.
- `controller/ClienteController`: capa REST para operaciones CRUD de clientes.
- `service/ClienteService`: lógica de negocio, validación de unicidad de RUT, normalización de campos y conversión a DTO.
- `repository/ClienteRepository`: consultas JPA (`findByRut`, `existsByRut`).
- `model/Cliente`: entidad persistente de cliente.
- `dto/ClienteRequest`: entrada validada (`@NotBlank`, `@Email`).
- `dto/ClienteResponse`: salida de datos del cliente.
- `exception/ConflictException`: conflicto de negocio (ej. RUT duplicado).
- `exception/ResourceNotFoundException`: recurso inexistente (ID/RUT no encontrado).

## 9) Endpoints disponibles
Base URL local: `http://localhost:8081/api/clientes`

- `POST /api/clientes` -> crea cliente.
- `GET /api/clientes` -> lista clientes.
- `GET /api/clientes/{id}` -> obtiene cliente por ID.
- `GET /api/clientes/rut/{rut}` -> obtiene cliente por RUT.
- `PUT /api/clientes/{id}` -> actualiza cliente.
- `DELETE /api/clientes/{id}` -> elimina cliente.

## 10) Ejemplos para probar con Postman
### Crear cliente
**Request**
- Método: `POST`
- URL: `http://localhost:8081/api/clientes`
- Header: `Content-Type: application/json`

```json
{
  "nombre": "Juan Perez",
  "rut": "12345678-9",
  "email": "juan.perez@correo.cl",
  "telefono": "+56911112222",
  "direccion": "Av. Siempre Viva 123",
  "comuna": "Santiago",
  "region": "Metropolitana"
}
```

**Response esperado (201 Created)**

```json
{
  "id": 1,
  "nombre": "Juan Perez",
  "rut": "12345678-9",
  "email": "juan.perez@correo.cl",
  "telefono": "+56911112222",
  "direccion": "Av. Siempre Viva 123",
  "comuna": "Santiago",
  "region": "Metropolitana",
  "fechaRegistro": "2026-05-11"
}
```

### Listar clientes
- Método: `GET`
- URL: `http://localhost:8081/api/clientes`

**Response esperado (200 OK)**

```json
[
  {
    "id": 1,
    "nombre": "Juan Perez",
    "rut": "12345678-9",
    "email": "juan.perez@correo.cl",
    "telefono": "+56911112222",
    "direccion": "Av. Siempre Viva 123",
    "comuna": "Santiago",
    "region": "Metropolitana",
    "fechaRegistro": "2026-05-11"
  }
]
```

### Obtener por ID
- Método: `GET`
- URL: `http://localhost:8081/api/clientes/1`

### Obtener por RUT
- Método: `GET`
- URL: `http://localhost:8081/api/clientes/rut/12345678-9`

### Actualizar cliente
- Método: `PUT`
- URL: `http://localhost:8081/api/clientes/1`
- Header: `Content-Type: application/json`

```json
{
  "nombre": "Juan Perez Soto",
  "rut": "12345678-9",
  "email": "juan.soto@correo.cl",
  "telefono": "+56933334444",
  "direccion": "Av. Nueva 456",
  "comuna": "Providencia",
  "region": "Metropolitana"
}
```

### Eliminar cliente
- Método: `DELETE`
- URL: `http://localhost:8081/api/clientes/1`
- Response esperado: `204 No Content`

## 11) Ejemplos JSON de request y response
### ClienteRequest (entrada)

```json
{
  "nombre": "Maria Gonzalez",
  "rut": "98765432-1",
  "email": "maria.gonzalez@correo.cl",
  "telefono": "+56977778888",
  "direccion": "Calle 100",
  "comuna": "Valparaiso",
  "region": "Valparaiso"
}
```

### ClienteResponse (salida)

```json
{
  "id": 2,
  "nombre": "Maria Gonzalez",
  "rut": "98765432-1",
  "email": "maria.gonzalez@correo.cl",
  "telefono": "+56977778888",
  "direccion": "Calle 100",
  "comuna": "Valparaiso",
  "region": "Valparaiso",
  "fechaRegistro": "2026-05-11"
}
```

## 12) Verificar datos en H2 Console
1. Iniciar el servicio.
2. Abrir: `http://localhost:8081/h2-console`.
3. Usar los siguientes parámetros:
   - JDBC URL: `jdbc:h2:mem:cliente_db`
   - User Name: `sa`
   - Password: *(vacío)*
4. Ejecutar consulta:

```sql
SELECT * FROM clientes;
```

## 13) Errores esperados
- **400 Bad Request**: validación de `ClienteRequest` (campos obligatorios vacíos o email inválido).
- **404 Not Found**: cliente no encontrado por ID o RUT (`ResourceNotFoundException`).
- **409 Conflict**: intento de registrar/actualizar con RUT ya existente (`ConflictException`).
- **500 Internal Server Error**: error no controlado en la aplicación o infraestructura.

## 14) Cómo ejecutar el microservicio
Desde la raíz del proyecto `cliente-service`:

```bash
mvn clean compile
mvn spring-boot:run
```

## 15) Conexión con otros microservicios (FeignClient)
- El proyecto incluye dependencia **OpenFeign** en `pom.xml`.
- En el estado actual **no hay interfaces `@FeignClient` implementadas** en este microservicio.
- Cuando se incorporen clientes Feign, la integración debe ubicarse idealmente en `client/` y consumirse desde `service/`.

## 16) Orden de ejecución junto con otros servicios

Para un correcto funcionamiento del ecosistema de microservicios, ejecuta los servicios en este orden:

| Orden | Servicio | Puerto | Comando |
|-------|----------|--------|---------|
| 1 | **cliente-service** | **8081** | `mvn spring-boot:run` |
| 2 | producto-service | 8082 | `mvn spring-boot:run` |
| 3 | estado-service | 8084 | `mvn spring-boot:run` |
| 4 | pedido-service | 8083 | `mvn spring-boot:run` |

**Nota:** `cliente-service` es el primer servicio base independiente. El orden 4 (pedido-service) es última porque depende de los otros tres mediante FeignClient.

