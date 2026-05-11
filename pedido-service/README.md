# pedido-service - Microservicio de Gestión de Pedidos

## 1. Descripción General

**pedido-service** es un microservicio Spring Boot responsable de la gestión integral de pedidos dentro del sistema de comercio electrónico distribuido. Proporciona funcionalidades para crear, consultar, actualizar el estado y eliminar pedidos, además de mantener un historial de cambios de estado de cada pedido.

### Objetivo del Microservicio

Gestionar el ciclo de vida completo de los pedidos, desde su creación hasta su entrega, permitiendo:
- Crear nuevos pedidos validando clientes y productos disponibles
- Consultar pedidos por número, cliente o listar todos
- Actualizar el estado de los pedidos
- Mantener un historial auditable de cambios de estado
- Eliminar pedidos si es necesario

### Problema que Resuelve

En un sistema modular de microservicios, pedido-service centraliza toda la lógica de gestión de pedidos, evitando duplicación de código y garantizando consistencia. Se comunica de manera sincrónica con otros servicios (cliente-service, producto-service y estado-service) mediante **FeignClient**, permitiendo que cada microservicio mantenga su independencia sin crear acoplamiento innecesario.

---

## 2. Configuración y Ejecución

### Puerto

**Puerto predeterminado:** `8083`

Configurado en `src/main/resources/application.properties`:
```properties
server.port=8083
```

### Base de Datos

**Motor:** H2 Database (en memoria)  
**Base de datos:** `pedido_db`  
**Usuario:** `sa`  
**Contraseña:** (vacía)

**Configuración en `application.properties`:**
```properties
spring.datasource.url=jdbc:h2:mem:pedido_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### Acceder a H2 Console

1. Ejecutar el microservicio
2. Abrir en el navegador: `http://localhost:8083/h2-console`
3. Ingresar credenciales:
   - **URL JDBC:** `jdbc:h2:mem:pedido_db`
   - **Usuario:** `sa`
   - **Contraseña:** (dejar vacía)
4. Hacer clic en `Connect`

**Tablas disponibles:**
- `pedidos` - Almacena los pedidos
- `items_pedido` - Almacena los ítems de cada pedido

---

## 3. Dependencias Principales

| Dependencia | Versión | Propósito |
|---|---|---|
| **Spring Boot Starter Web** | 4.0.5 | Framework web y REST |
| **Spring Boot Starter Data JPA** | 4.0.5 | Persistencia de datos y ORM Hibernate |
| **H2 Database** | Latest | Base de datos en memoria |
| **MySQL Connector J** | Latest | Driver MySQL (soporte futuro) |
| **Spring Boot Starter Validation** | 4.0.5 | Validación de entidades con Jakarta Validation |
| **Lombok** | Latest | Generación automática de getters/setters |
| **Spring Cloud OpenFeign** | 2025.1.1 | Cliente HTTP declarativo para microservicios |
| **Spring Boot DevTools** | 4.0.5 | Recarga automática durante desarrollo |

**Versión de Java:** 25

---

## 4. Arquitectura Interna

```
pedido-service/
├── client/                          # Clientes Feign para otros microservicios
│   ├── ClienteFeignClient.java     # Cliente para consultar clientes
│   ├── ProductoFeignClient.java    # Cliente para consultar productos
│   ├── EstadoFeignClient.java      # Cliente para registrar y consultar cambios de estado
│   ├── cliente/dto/
│   │   └── ClienteResponse.java    # DTO de respuesta de cliente-service
│   ├── producto/dto/
│   │   └── ProductoResponse.java   # DTO de respuesta de producto-service
│   └── estado/dto/
│       ├── CambioEstadoRequest.java    # DTO para registrar cambio de estado
│       └── CambioEstadoResponse.java   # DTO de respuesta de cambio de estado
├── controller/
│   └── PedidoController.java       # Endpoints REST
├── dto/                             # Data Transfer Objects
│   ├── PedidoRequest.java          # DTO para crear pedido
│   ├── PedidoResponse.java         # DTO de respuesta de pedido
│   ├── ItemPedidoRequest.java      # DTO para ítem de pedido (request)
│   ├── ItemPedidoResponse.java     # DTO para ítem de pedido (response)
│   └── EstadoRequest.java          # DTO para actualizar estado
├── model/                           # Entidades JPA
│   ├── Pedido.java                 # Entidad de pedido
│   └── ItemPedido.java             # Entidad de ítem de pedido
├── repository/
│   └── PedidoRepository.java       # Interfaz JPA para persistencia
├── service/
│   └── PedidoService.java          # Lógica de negocio
├── common/exception/               # Excepciones personalizadas
│   ├── ResourceNotFoundException.java
│   ├── ServiceUnavailableException.java
│   └── ConflictException.java
└── PedidoServiceApplication.java   # Clase principal con @SpringBootApplication
```

---

## 5. Descripción de Clases Principales

### `PedidoServiceApplication.java`
**Responsabilidad:** Clase principal de Spring Boot que inicia la aplicación.

**Anotaciones:**
- `@SpringBootApplication` - Habilita autoconfiguración de Spring
- `@EnableFeignClients(basePackages = "cl.duocuc.pedidoservice.client")` - Habilita clientes Feign

### `PedidoController.java`
**Responsabilidad:** Expone los endpoints HTTP REST para gestionar pedidos.

**Métodos principales:**
- `POST /api/pedidos` - Crear pedido
- `GET /api/pedidos` - Listar todos los pedidos
- `GET /api/pedidos/{numeroPedido}` - Obtener pedido por número (Long)
- `GET /api/pedidos/numero/{numeroPedido}` - Obtener pedido por número (String)
- `GET /api/pedidos/cliente/{clienteId}` - Listar pedidos de un cliente
- `PATCH /api/pedidos/{numeroPedido}/estado` - Actualizar estado del pedido
- `GET /api/pedidos/{numeroPedido}/historial` - Obtener historial de cambios de estado
- `DELETE /api/pedidos/{numeroPedido}` - Eliminar un pedido

### `PedidoService.java`
**Responsabilidad:** Implementa la lógica de negocio para gestionar pedidos.

**Funcionalidades principales:**
1. **Validación de cliente** - Consulta `cliente-service` mediante `ClienteFeignClient`
2. **Validación de productos** - Consulta `producto-service` mediante `ProductoFeignClient` para cada ítem
3. **Cálculo de totales** - Calcula subtotal por ítem y monto total del pedido
4. **Gestión de estado** - Registra cambios de estado en `estado-service` mediante `EstadoFeignClient`
5. **Caché en memoria** - Optimiza consultas frecuentes usando `ConcurrentHashMap`
6. **Manejo de excepciones Feign** - Traduce errores de comunicación a excepciones específicas

### `Pedido.java` (Entidad JPA)
**Responsabilidad:** Representa un pedido en la base de datos.

**Atributos:**
- `numeroPedido` (Long) - Identificador único (generado automáticamente)
- `clienteId` (Long) - Referencia al cliente que realizó el pedido
- `estado` (String) - Estado actual (COLA, PRODUCCION, LISTO, DESPACHADO, ENTREGADO)
- `tipoDespacho` (String) - Tipo de envío
- `monto` (Double) - Monto total del pedido
- `fechaCreacion` (LocalDateTime) - Fecha de creación
- `items` (List<ItemPedido>) - Lista de ítems del pedido

### `ItemPedido.java` (Entidad JPA)
**Responsabilidad:** Representa un producto dentro de un pedido.

**Atributos:**
- `id` (Long) - Identificador del ítem
- `productoId` (Long) - Referencia al producto
- `nombreProducto` (String) - Nombre del producto (obtenido de producto-service)
- `cantidad` (Integer) - Cantidad solicitada
- `precioUnitario` (Double) - Precio unitario (obtenido de producto-service)
- `subtotal` (Double) - Cantidad × Precio unitario

### Clientes Feign

#### `ClienteFeignClient.java`
Comunica con `cliente-service` (Puerto 8081) para validar la existencia de clientes.

#### `ProductoFeignClient.java`
Comunica con `producto-service` (Puerto 8082) para obtener información de productos.

#### `EstadoFeignClient.java`
Comunica con `estado-service` (Puerto 8084) para gestionar historial de cambios de estado.

---

## 6. Endpoints Disponibles

### 1. Crear Pedido

**Método:** `POST`  
**Endpoint:** `/api/pedidos`  
**Descripción:** Crea un nuevo pedido validando cliente y productos disponibles.

**Request Body:**
```json
{
  "clienteId": 1,
  "estado": "COLA",
  "tipoDespacho": "NORMAL",
  "items": [
    {
      "productoId": 1,
      "cantidad": 2
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "numeroPedido": 1,
  "clienteId": 1,
  "estado": "COLA",
  "tipoDespacho": "NORMAL",
  "monto": 100000.0,
  "fechaCreacion": "2026-05-11T03:35:00",
  "items": [
    {
      "id": 1,
      "productoId": 1,
      "nombreProducto": "Laptop",
      "cantidad": 2,
      "precioUnitario": 50000.0,
      "subtotal": 100000.0
    }
  ]
}
```

---

### 2. Listar Todos los Pedidos

**Método:** `GET`  
**Endpoint:** `/api/pedidos`  
**Descripción:** Obtiene la lista de todos los pedidos en el sistema.

---

### 3. Obtener Pedido por Número

**Método:** `GET`  
**Endpoint:** `/api/pedidos/{numeroPedido}`  
**Descripción:** Obtiene un pedido específico por su número.

---

### 4. Listar Pedidos por Cliente

**Método:** `GET`  
**Endpoint:** `/api/pedidos/cliente/{clienteId}`  
**Descripción:** Obtiene todos los pedidos de un cliente específico.

---

### 5. Actualizar Estado del Pedido

**Método:** `PATCH`  
**Endpoint:** `/api/pedidos/{numeroPedido}/estado`  
**Descripción:** Cambia el estado de un pedido.

**Request Body:**
```json
{
  "estado": "PRODUCCION"
}
```

**Estados válidos:** COLA, PRODUCCION, LISTO, DESPACHADO, ENTREGADO

---

### 6. Obtener Historial de Cambios de Estado

**Método:** `GET`  
**Endpoint:** `/api/pedidos/{numeroPedido}/historial`  
**Descripción:** Obtiene el historial de cambios de estado de un pedido.

---

### 7. Eliminar Pedido

**Método:** `DELETE`  
**Endpoint:** `/api/pedidos/{numeroPedido}`  
**Descripción:** Elimina un pedido del sistema.

---

## 7. Ejemplos Postman

### Crear Pedido
```http
POST http://localhost:8083/api/pedidos
Content-Type: application/json

{
  "clienteId": 1,
  "estado": "COLA",
  "tipoDespacho": "NORMAL",
  "items": [
    {
      "productoId": 1,
      "cantidad": 2
    }
  ]
}
```

### Listar Todos los Pedidos
```http
GET http://localhost:8083/api/pedidos
```

### Obtener Pedido Específico
```http
GET http://localhost:8083/api/pedidos/1
```

### Actualizar Estado
```http
PATCH http://localhost:8083/api/pedidos/1/estado
Content-Type: application/json

{
  "estado": "PRODUCCION"
}
```

### Ver Historial
```http
GET http://localhost:8083/api/pedidos/1/historial
```

### Eliminar Pedido
```http
DELETE http://localhost:8083/api/pedidos/1
```

---

## 8. Códigos de Error

### 400 - Bad Request
Parámetros inválidos o validación fallida.

### 404 - Not Found
Pedido, cliente o producto no encontrado.

### 409 - Conflict
Violación de restricción de negocio (estado inválido).

### 500 - Internal Server Error
Error en el servidor o en la comunicación con otros microservicios.

---

## 9. Cómo Ejecutar

### Compilar
```bash
cd C:\Users\sebyv\IdeaProjects\pedido-service
.\mvnw.cmd clean compile
```

### Ejecutar
```bash
.\mvnw.cmd spring-boot:run
```

O empaquetar y ejecutar:
```bash
.\mvnw.cmd clean package -DskipTests
java -jar target/pedido-service-0.0.1-SNAPSHOT.jar
```

### Verificar que está ejecutándose
```bash
curl http://localhost:8083/api/pedidos
```

---

## 10. Integración con Otros Microservicios

pedido-service se comunica con tres microservicios mediante **OpenFeign**:

- **cliente-service** (Puerto 8081) - Validar clientes
- **producto-service** (Puerto 8082) - Obtener datos de productos
- **estado-service** (Puerto 8084) - Registrar y consultar historial de estados

### Orden de Ejecución Recomendado

Para un correcto funcionamiento del ecosistema de microservicios, ejecuta los servicios en este orden:

| Orden | Servicio | Puerto | Comando |
|-------|----------|--------|---------|
| 1 | cliente-service | 8081 | `mvn spring-boot:run` |
| 2 | producto-service | 8082 | `mvn spring-boot:run` |
| 3 | estado-service | 8084 | `mvn spring-boot:run` |
| 4 | **pedido-service** | **8083** | `mvn spring-boot:run` ← Este servicio |

**Nota importante:** `pedido-service` **DEBE iniciarse al último** porque depende de los otros tres servicios mediante FeignClient (cliente-service, producto-service, estado-service).

---

## 11. Integración Feign - Validación de Datos

### Creación de Pedido

- **Validar cliente:** Consulta `cliente-service` para verificar que el cliente existe
- **Validar productos:** Consulta `producto-service` para cada ítem, obteniendo nombre y precio
- **Si cliente-service responde 404:** Lanza `ResourceNotFoundException` con mensaje "Cliente no encontrado con id: X"
- **Si producto-service responde 404:** Lanza `ResourceNotFoundException` con mensaje "Producto no encontrado con id: X"
- **Si hay error de comunicación:** Lanza `ServiceUnavailableException` con mensaje "No se pudo conectar con el microservicio correspondiente"

### Cambio de Estado

- Actualiza el estado en la BD
- Registra el cambio en `estado-service` mediante FeignClient
- No crea duplicados si el estado no cambió

### Consulta de Historial

- Obtiene el historial desde `estado-service`, no desde BD local
- Permite auditoría centralizada

---

## 12. Configuración (application.properties)

```properties
server.port=8083
services.cliente.url=http://localhost:8081
services.producto.url=http://localhost:8082
services.estado.url=http://localhost:8084

# H2 Database
spring.datasource.url=jdbc:h2:mem:pedido_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

---

## 13. Notas Importantes

- pedido-service **NO contiene lógica de cliente, producto o estado**
- Toda consulta a estos datos se realiza mediante FeignClient
- Si otro servicio falla, pedido-service lo reporta claramente
- Mantiene caché en memoria de pedidos consultados recientemente
- Cada cambio de estado se audita en estado-service

---

## 14. Versión

**Versión:** 1.0  
**Java Version:** 25  
**Spring Boot Version:** 4.0.5  
**Última actualización:** 2026-05-11
