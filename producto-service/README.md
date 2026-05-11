# producto-service

## Ecosistema de microservicios

Este microservicio es parte de un ecosistema distribuido donde cada servicio se ejecuta en un puerto único:

| Servicio | Puerto | Descripción |
|---|---|---|
| cliente-service | 8081 | Administración de clientes |
| **producto-service** | **8082** | Administración de productos ← Este servicio |
| pedido-service | 8083 | Administración de pedidos |
| estado-service | 8084 | Administración de estados | |

---

## 1. Descripción general

**`producto-service`** es un microservicio Spring Boot responsable de la administración de productos dentro del ecosistema de la plataforma. Expone una API REST para crear, consultar, actualizar, listar y eliminar productos, aplicando validaciones de negocio básicas y persistiendo la información en una base de datos H2 en memoria.

Este microservicio resuelve la necesidad de centralizar el catálogo de productos en un servicio independiente, desacoplado del resto del sistema, facilitando su mantenimiento, escalabilidad y futura integración con otros dominios mediante clientes Feign.

## 2. Objetivo del microservicio

El objetivo principal de `producto-service` es administrar el ciclo de vida de los productos del sistema:

- registrar nuevos productos,
- consultar productos por identificador, nombre o categoría,
- actualizar información existente,
- eliminar productos no requeridos,
- validar reglas de negocio mínimas como precio mayor a cero y stock no negativo.

## 3. Problema que resuelve dentro del sistema

En una arquitectura basada en microservicios, el catálogo de productos no debe depender de un monolito central. Este servicio resuelve ese problema al encapsular la lógica de productos en una unidad autónoma, con su propia capa de persistencia, API y modelo de datos.

De esta forma:

- se evita mezclar responsabilidades con otros dominios,
- se simplifica el mantenimiento del catálogo,
- se reduce el acoplamiento entre componentes,
- se habilita la evolución independiente del servicio.

## 4. Puerto de ejecución

El servicio se ejecuta en el puerto **8082**.

```properties
server.port=8082
```

## 5. Base de datos utilizada

El microservicio utiliza **H2 en memoria** para desarrollo y pruebas locales.

### Configuración principal

```properties
spring.datasource.url=jdbc:h2:mem:producto_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.properties.hibernate.format_sql=true
```

### Consola H2

- URL: `http://localhost:8082/h2-console`
- JDBC URL: `jdbc:h2:mem:producto_db`
- Usuario: `sa`
- Contraseña: vacía

> La base de datos es temporal: su contenido se pierde al reiniciar el microservicio.

## 6. Dependencias principales

El servicio está preparado con las siguientes dependencias principales:

- **Spring Web**: expone la API REST.
- **Spring Data JPA**: administra persistencia y consultas.
- **H2**: base de datos en memoria para ejecución local.
- **MySQL Driver**: incluido para escenarios donde el servicio se despliegue con MySQL en entornos distintos al local.
- **Validation**: validación de entrada mediante anotaciones Jakarta Validation.
- **Lombok**: reduce código repetitivo en DTOs y entidad.
- **OpenFeign**: habilitado para integraciones futuras con otros microservicios.

## 7. Arquitectura interna

La estructura del microservicio está organizada por capas:

### `controller`
Expone los endpoints REST y recibe las peticiones HTTP.

### `dto`
Contiene los objetos de transferencia de datos para entrada y salida.

### `model`
Define la entidad persistente que representa un producto en la base de datos.

### `repository`
Permite el acceso a datos mediante Spring Data JPA.

### `service`
Implementa la lógica de negocio y coordina las operaciones entre controller y repository.

### `client`
En la implementación actual **no existe un paquete `client` con consumidores Feign**, aunque la aplicación tiene `@EnableFeignClients` habilitado para futuras integraciones.

### `common.exception`
Agrupa excepciones de negocio reutilizables.

## 8. Clases principales y responsabilidad de cada una

### `ProductoServiceApplication`
Clase principal de arranque del microservicio. Contiene `main()` y `@SpringBootApplication`, por lo que Spring realiza el escaneo del paquete raíz `cl.duocuc.productoservice`.

### `ProductoController`
Controlador REST ubicado en `/api/productos`. Expone los endpoints públicos para crear, consultar, listar, actualizar y eliminar productos.

### `ProductoService`
Contiene la lógica de negocio principal:

- normalización de texto,
- validación de precio y stock,
- verificación de duplicados por nombre,
- búsqueda por ID, nombre y categoría,
- transformación de entidad a respuesta.

### `ProductoRepository`
Repositorio JPA para acceso a datos de `Producto`. Incluye consultas derivadas por nombre y categoría.

### `Producto`
Entidad JPA que representa la tabla `productos`.

### `ProductoRequest`
DTO de entrada para crear y actualizar productos.

### `ProductoResponse`
DTO de salida con la información que retorna la API.

### `ConflictException`
Excepción de negocio para conflictos como nombre duplicado o reglas de validación incumplidas.

### `ResourceNotFoundException`
Excepción de negocio para recursos inexistentes, por ejemplo, cuando no se encuentra un producto por ID o nombre.

## 9. Endpoints disponibles

Base path: **`/api/productos`**

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/productos` | Crear un producto |
| GET | `/api/productos` | Listar todos los productos |
| GET | `/api/productos/{id}` | Obtener un producto por ID |
| GET | `/api/productos/nombre/{nombre}` | Buscar un producto por nombre |
| GET | `/api/productos/categoria/{categoria}` | Listar productos por categoría |
| PUT | `/api/productos/{id}` | Actualizar un producto |
| DELETE | `/api/productos/{id}` | Eliminar un producto |

## 10. Qué hace cada endpoint

### 10.1 Crear producto

**POST** `/api/productos`

Registra un nuevo producto validando:

- nombre obligatorio,
- categoría obligatoria,
- precio obligatorio y mayor a cero,
- stock obligatorio y no negativo,
- nombre único (sin distinguir mayúsculas/minúsculas).

#### Request JSON

```json
{
  "nombre": "Notebook Lenovo",
  "descripcion": "Equipo portátil para trabajo y estudio",
  "categoria": "Tecnología",
  "precio": 749990.0,
  "stock": 15
}
```

#### Response JSON

```json
{
  "id": 1,
  "nombre": "Notebook Lenovo",
  "descripcion": "Equipo portátil para trabajo y estudio",
  "categoria": "Tecnología",
  "precio": 749990.0,
  "stock": 15,
  "fechaCreacion": "2026-05-11T10:15:30.123"
}
```

#### Respuesta esperada

- **201 Created**

---

### 10.2 Listar productos

**GET** `/api/productos`

Retorna todos los productos registrados.

#### Response JSON

```json
[
  {
    "id": 1,
    "nombre": "Notebook Lenovo",
    "descripcion": "Equipo portátil para trabajo y estudio",
    "categoria": "Tecnología",
    "precio": 749990.0,
    "stock": 15,
    "fechaCreacion": "2026-05-11T10:15:30.123"
  }
]
```

#### Respuesta esperada

- **200 OK**

---

### 10.3 Obtener producto por ID

**GET** `/api/productos/{id}`

Retorna un producto específico por su identificador.

#### Ejemplo

`GET http://localhost:8082/api/productos/1`

#### Respuesta esperada

- **200 OK**
- **404 Not Found** si el producto no existe

---

### 10.4 Buscar producto por nombre

**GET** `/api/productos/nombre/{nombre}`

Busca un producto por nombre, ignorando mayúsculas/minúsculas.

#### Ejemplo

`GET http://localhost:8082/api/productos/nombre/Notebook%20Lenovo`

#### Respuesta esperada

- **200 OK**
- **404 Not Found** si no existe coincidencia

---

### 10.5 Listar productos por categoría

**GET** `/api/productos/categoria/{categoria}`

Retorna los productos filtrados por categoría.

#### Ejemplo

`GET http://localhost:8082/api/productos/categoria/Tecnología`

#### Respuesta esperada

- **200 OK**

---

### 10.6 Actualizar producto

**PUT** `/api/productos/{id}`

Actualiza un producto existente y mantiene las mismas reglas de validación del alta.

#### Request JSON

```json
{
  "nombre": "Notebook Lenovo ThinkPad",
  "descripcion": "Equipo portátil de alto rendimiento",
  "categoria": "Tecnología",
  "precio": 899990.0,
  "stock": 10
}
```

#### Response JSON

```json
{
  "id": 1,
  "nombre": "Notebook Lenovo ThinkPad",
  "descripcion": "Equipo portátil de alto rendimiento",
  "categoria": "Tecnología",
  "precio": 899990.0,
  "stock": 10,
  "fechaCreacion": "2026-05-11T10:15:30.123"
}
```

#### Respuesta esperada

- **200 OK**
- **404 Not Found** si el producto no existe
- **409 Conflict** si el nuevo nombre ya existe en otro registro

---

### 10.7 Eliminar producto

**DELETE** `/api/productos/{id}`

Elimina un producto existente.

#### Respuesta esperada

- **204 No Content**
- **404 Not Found** si el producto no existe

## 11. Ejemplos para probar con Postman

### Crear producto

- **Method:** `POST`
- **URL:** `http://localhost:8082/api/productos`
- **Headers:** `Content-Type: application/json`
- **Body:** raw JSON

```json
{
  "nombre": "Mouse inalámbrico",
  "descripcion": "Mouse ergonómico con conexión Bluetooth",
  "categoria": "Accesorios",
  "precio": 19990.0,
  "stock": 50
}
```

### Consultar todos los productos

- **Method:** `GET`
- **URL:** `http://localhost:8082/api/productos`

### Consultar por ID

- **Method:** `GET`
- **URL:** `http://localhost:8082/api/productos/1`

### Consultar por nombre

- **Method:** `GET`
- **URL:** `http://localhost:8082/api/productos/nombre/Mouse%20inalámbrico`

### Consultar por categoría

- **Method:** `GET`
- **URL:** `http://localhost:8082/api/productos/categoria/Accesorios`

### Actualizar producto

- **Method:** `PUT`
- **URL:** `http://localhost:8082/api/productos/1`
- **Headers:** `Content-Type: application/json`
- **Body:** raw JSON

```json
{
  "nombre": "Mouse inalámbrico Pro",
  "descripcion": "Versión mejorada con mayor autonomía",
  "categoria": "Accesorios",
  "precio": 24990.0,
  "stock": 40
}
```

### Eliminar producto

- **Method:** `DELETE`
- **URL:** `http://localhost:8082/api/productos/1`

## 12. Verificación de datos en H2 Console

Para revisar los datos almacenados:

1. Iniciar el microservicio.
2. Abrir la consola H2 en:
   `http://localhost:8082/h2-console`
3. Completar los campos de conexión:
   - **JDBC URL:** `jdbc:h2:mem:producto_db`
   - **User Name:** `sa`
   - **Password:** vacío
4. Presionar **Connect**.
5. Ejecutar consultas SQL sobre la tabla `productos`.

### Consulta de ejemplo

```sql
SELECT * FROM productos;
```

## 13. Errores esperados

> Los códigos HTTP indicados corresponden al comportamiento esperado del servicio según las reglas de negocio implementadas.

### 400 Bad Request — Validación

Se produce cuando el request no cumple las validaciones de entrada:

- nombre vacío,
- categoría vacía,
- precio nulo o menor/igual a cero,
- stock nulo o negativo.

### 404 Not Found — Recurso no encontrado

Se produce cuando:

- no existe un producto con el ID solicitado,
- no existe un producto con el nombre solicitado.

### 409 Conflict — Conflicto

Se produce cuando:

- se intenta crear un producto con un nombre ya existente,
- se intenta actualizar un producto usando un nombre que ya pertenece a otro registro,
- el precio es inválido,
- el stock es inválido.

### 500 Internal Server Error — Error interno

Se produce ante fallos no controlados en tiempo de ejecución, por ejemplo:

- errores inesperados de persistencia,
- problemas de infraestructura,
- excepciones no contempladas por la capa de manejo global.

## 14. Ejecución del microservicio

### Compilar

```bash
mvn clean compile
```

### Ejecutar

```bash
mvn spring-boot:run
```

> El proyecto está configurado para Java 25 y Spring Boot 4.0.5.

## 15. Integración con otros microservicios usando Feign

La aplicación tiene `@EnableFeignClients` habilitado en `ProductoServiceApplication`, lo que deja preparada la infraestructura para consumir otros servicios mediante Feign Client.

Sin embargo, en el código actual **no existe un paquete `client` ni interfaces `@FeignClient` implementadas** en `producto-service`.

### Consumo de producto-service desde otros servicios

Otros microservicios del ecosistema pueden consumir `producto-service` mediante Feign:

- **cliente-service** (8081) puede consultar productos asociados a clientes,
- **pedido-service** (8083) puede validar productos y stocks antes de crear pedidos,
- **estado-service** (8084) puede referenciar productos en eventos de ciclo de vida.

### Cliente Feign de ejemplo

Para que otro servicio consuma `producto-service`, debe definir una interfaz como:

```java
@FeignClient(name = "producto-service", url = "http://localhost:8082")
public interface ProductoClient {
    
    @GetMapping("/api/productos/{id}")
    ProductoResponse obtenerProducto(@PathVariable Long id);
    
    @GetMapping("/api/productos")
    List<ProductoResponse> listarProductos();
}
```

### Notas de integración

- Por ahora, `producto-service` **no consume otros servicios**.
- En el futuro, puede ser consumido por `pedido-service`, `cliente-service` o `estado-service`.
- La estructura queda lista para incorporar consumidores Feign sin cambiar el arranque de la aplicación.
- Para que Feign funcione correctamente, todos los servicios deben estar ejecutándose en sus puertos correspondientes.

## 16. Orden de ejecución junto con los demás servicios

Para un correcto funcionamiento del ecosistema de microservicios, ejecuta los servicios en este orden:

| Orden | Servicio | Puerto | Comando |
|-------|----------|--------|---------|
| 1 | cliente-service | 8081 | `mvn spring-boot:run` |
| 2 | **producto-service** | **8082** | `mvn spring-boot:run` ← Este servicio |
| 3 | estado-service | 8084 | `mvn spring-boot:run` |
| 4 | pedido-service | 8083 | `mvn spring-boot:run` |

**Nota:** `producto-service` es un servicio base independiente. En el estado actual funciona autónomamente sobre H2 en memoria. El orden 4 (pedido-service) es última porque depende de los otros tres mediante FeignClient.

## 17. Resumen técnico

- **Nombre del servicio:** `producto-service`
- **Package base:** `cl.duocuc.productoservice`
- **Puerto:** `8082`
- **Entorno:** parte del ecosistema de 4 microservicios
- **Base de datos:** H2 en memoria (`producto_db`)
- **Capas:** controller, dto, model, repository, service, common.exception
- **Integración futura:** OpenFeign habilitado para ser consumido por otros servicios
- **Servicios en el ecosistema:**
  - cliente-service (8081)
  - producto-service (8082) ← Este
  - pedido-service (8083)
  - estado-service (8084)

## 18. Notas finales

Este microservicio fue estructurado para mantener una separación clara de responsabilidades y facilitar su evolución dentro de una arquitectura de microservicios distribuida.

La API está diseñada para ser consumida fácilmente desde:

- Postman o herramientas de testing REST,
- Frontends web o móviles,
- Otros microservicios del ecosistema mediante Feign (`cliente-service`, `pedido-service`, `estado-service`).

La consola H2 permite inspeccionar rápidamente el estado de los datos durante el desarrollo. Todos los servicios deben ejecutarse en sus puertos correspondientes para que el ecosistema funcione correctamente.
