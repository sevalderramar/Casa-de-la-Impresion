# Módulo `pedido` — Documentación técnica

Este documento describe en detalle el módulo `pedido` de la aplicación `sistema-gestion-pedidos`.
Está dirigido a desarrolladores e ingenieros que mantendrán o integrarán con el módulo, y explica
su objetivo, estructura, entidades, reglas de negocio y endpoints REST.

---

1. Objetivo del módulo pedido
-----------------------------

 El módulo `pedido` gestiona la creación, consulta, actualización y eliminación física de pedidos y sus
 ítems asociados. Proporciona una API REST para operar sobre pedidos dentro de la aplicación monolítica
 modular. Está diseñado para soportar una futura separación a microservicios sin acoplarse directamente
 a otras áreas del dominio.

2. Qué problema resuelve
------------------------

 Permite almacenar y consultar la información de pedidos (cabecera y líneas de ítems), calcular montos
 automáticamente y controlar el estado operativo del pedido. Ofrece además una capa
de validación y reglas de negocio (por ejemplo, número de pedido único, cantidades y precios válidos)
para mantener la integridad de los datos.

3. Estructura del módulo
------------------------

El módulo está organizado por responsabilidades internas, siguiendo la convención:

- `controller` — Exposición REST y manejo de peticiones HTTP.
- `dto` — Objetos de transferencia (requests/responses) usados por los controladores.
- `model` — Entidades persistentes (JPA/Hibernate) que representan tablas en la base de datos.
- `repository` — Interfaces de acceso a datos (Spring Data JPA) para `Pedido` y `ItemPedido`.
- `service` — Lógica de negocio, validaciones y operaciones transaccionales.

4. Qué hace cada clase del módulo (visión general)
-------------------------------------------------

Nota: los nombres de clase citados corresponden a los archivos presentes en el proyecto bajo
`cl.duocuc.sistemagestionpedidos.pedido`.

- `controller/PedidoController.java`: expone los endpoints REST para crear, leer, filtrar, actualizar
  estado y eliminar pedidos. Convierte DTOs a entidades y delega la lógica al `PedidoService`.
- `dto/PedidoRequest.java`: DTO recibido en las peticiones de creación/actualización de pedido.
- `dto/PedidoResponse.java`: DTO devuelto en las respuestas con los datos de un pedido (incluye items).
- `dto/ItemPedidoRequest.java`: DTO para representar un ítem enviado al crear/actualizar un pedido.
- `dto/ItemPedidoResponse.java`: DTO devuelto en respuestas que representan un ítem de pedido.
  - `model/Pedido.java`: entidad JPA que representa la tabla de pedidos. Contiene atributos como
    `numeroPedido`, `clienteId`, `monto`, `items`, etc. Importante: este modelo ya no contiene campos
    relacionados con eliminación lógica.
- `model/ItemPedido.java`: entidad JPA que representa los ítems de un pedido (relación con `Pedido`).
- `repository/PedidoRepository.java`: interfaz Spring Data para consultas y persistencia de `Pedido`.
- `repository/ItemPedidoRepository.java` (si existe): interfaz para persistencia de `ItemPedido`.
 - `service/PedidoService.java`: contiene las reglas de negocio, validaciones y operaciones atómicas
  (crear pedido, calcular montos, cambiar estado y eliminación física mediante `deleteById`).

5. Entidad `Pedido` y sus atributos
----------------------------------

La entidad `Pedido` representa la cabecera de un pedido y contiene los siguientes atributos clave:

 - `numeroPedido` (Long): identificador único del pedido generado por la base de datos (PK).
 - `clienteId` (Long): referencia al cliente asociada al pedido (no es una relación JPA hacia `Cliente`).
 - `estado` (String / Enum): estado operativo del pedido (por ejemplo: `COLA`, `PRODUCCION`, `LISTO`, `DESPACHADO`, `ENTREGADO`).
- `tipoDespacho` (String / Enum): tipo de despacho (ej. `RETIRO`, `DOMICILIO`).
- `monto` (BigDecimal / Double): suma total de los subtotales de los ítems del pedido.
- `fechaCreacion` (LocalDateTime / Date): marca temporal de creación del pedido.
- `items` (List<ItemPedido>): lista de ítems asociados al pedido (relación uno-a-muchos en la entidad).

6. Entidad `ItemPedido` y sus atributos
--------------------------------------

Cada `ItemPedido` representa una línea del pedido y contiene:

- `id` (Long): identificador único del ítem.
- `productoId` (Long): identificador del producto (referencia por id, no relación directa a `Producto`).
- `nombreProducto` (String): nombre descriptivo del producto en el momento de la creación del pedido.
- `cantidad` (Integer): unidades solicitadas (debe ser mayor que 0).
- `precioUnitario` (BigDecimal / Double): precio por unidad (debe ser mayor que 0).
- `subtotal` (BigDecimal / Double): cálculo: `cantidad * precioUnitario`.

7. Por qué `Pedido` usa `clienteId` y no relación directa con `Cliente`
------------------------------------------------------------------

El módulo registra únicamente el identificador del cliente (`clienteId`) en lugar de mapear una relación
JPA directa con la entidad `Cliente` para mantener un bajo acoplamiento entre dominios. Razones:

- Permite que el módulo `pedido` funcione de forma independiente si el módulo `cliente` se convierte en
  un microservicio separado.
- Evita dependencias fuertes a nivel de entidad (cargas innecesarias, cascadas, problemas de sincronía).
- Facilita integraciones futuras mediante comunicación por API (REST) o messaging, en lugar de
  depender de clases y tablas compartidas.

8. Por qué todavía no valida contra `cliente-service`
---------------------------------------------------

En la implementación actual no existe integración runtime con un `cliente-service` externo; el sistema
almacena `clienteId` tal cual. Esto simplifica el despliegue local y las pruebas (no se requiere red ni
servicios adicionales). La validación remota de `clienteId` (comprobar existencia/estado del cliente) es
un siguiente paso planificado (ver sección "Próximos pasos").

9. Preparación para futura separación a microservicios
-----------------------------------------------------

El diseño con `clienteId` y la organización modular por dominio hacen que sea sencillo convertir cada
dominio (cliente, pedido, producto, despacho) en microservicios independientes. Puntos a favor:

- DTOs claros y fronteras bien definidas.
- Repositorios y entidades encapsulados dentro del módulo.
- `PedidoService` contiene la lógica del dominio, lo que facilita exponer la misma API desde un
  microservicio sin cambios en consumidores (salvo la ubicación del servicio).

10. Cálculo automático
----------------------

Las reglas automáticas incluidas son:

- `subtotal = cantidad * precioUnitario` (por cada `ItemPedido`).
- `monto = suma de subtotales` (suma de todos los `subtotal` de los ítems del pedido).

Estas operaciones se realizan en el `PedidoService` al crear o actualizar pedidos, garantizando
consistencia incluso si los valores unitarios se reciben desde el cliente.

11. Eliminación física
-----------------------

En esta versión se elimina el concepto de eliminación lógica. Las operaciones `DELETE` sobre pedidos
realizan una eliminación física utilizando `deleteById()` de JPA. Esto implica que:

- El registro en la tabla `PEDIDOS` y sus `ITEMS_PEDIDO` relacionados se eliminan de la base de datos.
- La relación uno-a-muchos está configurada con `cascade = CascadeType.ALL` y `orphanRemoval = true`, por
  lo que los `ItemPedido` asociados también se eliminan automáticamente.

Esta decisión se adoptó para alinear el modelo de datos con el flujo operacional real del negocio.

12. DTOs
--------

- `PedidoRequest`:
  - Representa la carga que el cliente HTTP envía para crear o actualizar un pedido.
  - Campos típicos: `clienteId`, `estado`, `tipoDespacho`, `items` (lista de `ItemPedidoRequest`).
   - `numeroPedido` no se envía en la creación: es generado automáticamente por la base de datos y devuelto en `PedidoResponse`.
   - No incluye campos calculados (`monto`, `fechaCreacion`) que son gestionados por el servidor.

- `PedidoResponse`:
   - Representa la respuesta devuelta al consumidor con todos los datos del pedido.
   - Incluye `numeroPedido` (identificador único, clave primaria generada por la base de datos), `clienteId`, `estado`, `tipoDespacho`, `monto`, `fechaCreacion` e `items` (lista de `ItemPedidoResponse`).

- `ItemPedidoRequest`:
  - Subconjunto de datos para crear/actualizar un ítem: `productoId`, `nombreProducto`, `cantidad`,
    `precioUnitario`.

- `ItemPedidoResponse`:
  - Datos devueltos para cada ítem, incluye `id`, `productoId`, `nombreProducto`, `cantidad`,
    `precioUnitario` y `subtotal`.

13. Repository
--------------

El `PedidoRepository` (y `ItemPedidoRepository` si está presente) son interfaces que extienden Spring
Data (por ejemplo `JpaRepository<Pedido, Long>`). Proveen:

- Operaciones CRUD estándar.
- Consultas derivadas por nombre.
- Posible paginación y ordenamiento para listados (`findAll(Pageable)`).

14. Service y reglas de negocio
-------------------------------

El `PedidoService` centraliza las reglas de negocio. Principales responsabilidades y validaciones:

- El `numeroPedido` es generado automáticamente por la base de datos (clave primaria, autoincremental).
- Validar que el pedido tenga al menos un item; si no, lanzar excepción.
- Validar que cada `cantidad` sea un entero positivo (> 0).
- Validar que cada `precioUnitario` sea positivo (> 0).
- Calcular `subtotal` por ítem y `monto` total del pedido antes de persistir.
- Asignar `fechaCreacion` al crear (si procede).
- Soportar actualización parcial del `estado` mediante un endpoint PATCH; validar estados
  permitidos y transiciones si aplica.
- Implementar eliminación física: los `DELETE` eliminan el pedido y sus ítems asociados.

15. Controller y endpoints
--------------------------

El controlador `PedidoController` expone los siguientes endpoints REST (rutas basadas en
`/api/pedidos`):

- POST /api/pedidos
  - Crea un nuevo pedido a partir de un `PedidoRequest`.
  - Valida reglas de negocio y devuelve `201 Created` con `PedidoResponse`.

- GET /api/pedidos
  - Lista pedidos.

 - GET /api/pedidos/{numeroPedido}
  - Obtiene un pedido por su `numeroPedido` (identificador único generado) y devuelve `PedidoResponse`.

- GET /api/pedidos/numero/{numeroPedido}
  - Busca un pedido por su `numeroPedido`.

- GET /api/pedidos/cliente/{clienteId}
  - Retorna pedidos asociados a un `clienteId`.

 - PATCH /api/pedidos/{numeroPedido}/estado
  - Actualiza el `estado` operativo del pedido. Ahora este endpoint recibe un cuerpo JSON con el nuevo estado:

    ```http
    PATCH /api/pedidos/{numeroPedido}/estado
    Content-Type: application/json

    {
      "estado": "LISTO"
    }
    ```

  - Valida la transición si existen reglas y persiste el cambio. Además registra automáticamente un `CambioEstado` en el módulo `estado`.

 - GET /api/pedidos/{numeroPedido}/historial
  - Devuelve el historial de cambios de estado del pedido consultando el módulo `estado`.
 
  - DELETE /api/pedidos/{numeroPedido}
    - Realiza eliminación física: borra el pedido y sus ítems asociados (`204 No Content`).

16. Ejemplos JSON para Postman
-----------------------------

- POST crear pedido (ejemplo válido):

```json
{
  "clienteId": 1,
  "estado": "COLA",
  "tipoDespacho": "RETIRO",
  "items": [
    {
      "productoId": 1,
      "nombreProducto": "Etiqueta ecocuero",
      "cantidad": 100,
      "precioUnitario": 250
    },
    {
      "productoId": 2,
      "nombreProducto": "Sticker redondo",
      "cantidad": 50,
      "precioUnitario": 150
    }
  ]
}
```

- PATCH actualizar estado (ejemplo):

PATCH /api/pedidos/123/estado

Content-Type: application/json

```
{
  "estado": "LISTO"
}
```

17. Cómo revisar datos en H2
---------------------------

Durante ejecución local la aplicación usa la base de datos en memoria H2. La consola web suele estar
disponible en `http://localhost:8080/h2-console` (ver `application.properties` para confirmar URL).

Tablas relevantes (nombres según mapeo JPA, típicamente en mayúsculas):

- `PEDIDOS` — tabla que almacena la entidad `Pedido`.
- `ITEMS_PEDIDO` — tabla que almacena los `ItemPedido` relacionados.

Ejemplo de consultas SQL en la consola H2:

```sql
SELECT * FROM PEDIDOS;
SELECT * FROM ITEMS_PEDIDO WHERE NUMERO_PEDIDO = 1;
```

18. Próximos pasos (mejoras y evolución)
---------------------------------------

Recomendaciones para evolución del módulo:

- Validar `clienteId` consultando el `cliente-service` (llamada HTTP a la API del servicio de clientes).
  Implementar reintentos y manejo de fallos (circuit breaker) antes de confiar en la validación remota.
- Agregar `producto-service` para validar `productoId` y obtener información actualizada del producto.
- Añadir un `despacho-service` o integraciones para coordinar logística, tipos de despacho y estados
  relacionados.
- Añadir métricas (Prometheus / Micrometer) para monitorear número de pedidos, tiempos de creación,
  errores y tasas de rechazo.
- Considerar eventos de dominio (ej. publicar evento "PedidoCreado") para integrar con otros servicios
  sin acoplamiento directo.

---

Anexos y notas:

- Mantener los DTOs y contratos REST estables facilitará la migración a microservicios.
- Documentar cualquier enumeración (`estado`, `tipoDespacho`) en el código y en el
  API documentation (ej. OpenAPI/Swagger) para que consumidores conozcan los valores permitidos.

Fin del documento.
