# Modulo `producto` - Documentacion tecnica

Este documento describe el modulo `producto` incorporado al monolito modular del proyecto.
Su diseno mantiene bajo acoplamiento y deja preparado el dominio para evolucionar a microservicios.

## 1. Objetivo del modulo producto

El modulo `producto` administra el catalogo de productos del sistema. Permite crear, consultar,
actualizar y eliminar (físicamente) productos mediante una API REST, manteniendo reglas de negocio
consistentes sobre nombre único, precio y stock.

## 2. Arquitectura del modulo

Ruta base del paquete:

`cl.duocuc.sistemagestionpedidos.producto`

Estructura:

- `controller`
- `dto`
- `model`
- `repository`
- `service`

No se crea microservicio separado; se mantiene la arquitectura modular existente dentro de la misma
aplicacion Spring Boot.

## 3. Responsabilidad de cada capa

- `controller`: expone endpoints REST con `ResponseEntity` y valida payloads con `@Valid`.
- `dto`: define contratos de entrada y salida (`ProductoRequest`, `ProductoResponse`).
- `model`: contiene la entidad JPA `Producto` mapeada en H2.
- `repository`: concentra acceso a datos con Spring Data JPA.
- `service`: implementa reglas de negocio y orquesta la persistencia.

## 4. Entidad `Producto` y atributos

La entidad `Producto` contiene:

- `id` (Long): identificador único.
- `nombre` (String): nombre comercial del producto.
- `descripcion` (String): descripción funcional/comercial.
- `categoria` (String): categoría del producto.
- `precio` (Double): precio unitario.
- `stock` (Integer): unidades disponibles.
- `fechaCreacion` (LocalDateTime): fecha y hora de alta del producto.

## 5. Reglas de negocio

- No se permiten productos duplicados por nombre (comparación case-insensitive).
- `fechaCreacion` se asigna automáticamente al crear.
- No se permite `precio` negativo ni cero.
- No se permite `stock` negativo.

## 6. Eliminación física

El endpoint DELETE elimina **completamente** el registro de la base de datos. La aplicación refleja
una política de gestión de datos donde los productos son eliminados cuando ya no se necesitan en el
catálogo. Si en el futuro se requiere auditoría histórica, se implementará una tabla separada.

## 7. Por qué producto está desacoplado del módulo pedido

Base URL: `/api/productos`

- `POST /api/productos`
- `GET /api/productos`
- `GET /api/productos/{id}`
- `GET /api/productos/nombre/{nombre}`
- `GET /api/productos/categoria/{categoria}`
- `PUT /api/productos/{id}`
- `DELETE /api/productos/{id}`

Estados HTTP:

- POST -> `201 Created`
- GET -> `200 OK`
- DELETE -> `204 No Content`

## 9. Ejemplos JSON para Postman

### POST crear producto

```json
{
  "nombre": "Etiqueta ecocuero",
  "descripcion": "Etiqueta premium para ropa",
  "categoria": "ETIQUETAS",
  "precio": 250.0,
  "stock": 1000
}
```

### PUT actualizar producto

```json
{
  "nombre": "Etiqueta ecocuero premium",
  "descripcion": "Etiqueta premium reforzada",
  "categoria": "ETIQUETAS",
  "precio": 290.0,
  "stock": 850
}
```

## 10. Errores HTTP posibles

- `400 Bad Request`: validaciones Jakarta (`@NotBlank`, `@Positive`, `@PositiveOrZero`).
- `404 Not Found`: producto inexistente o inactivo.
- `409 Conflict`: nombre duplicado o violacion de regla de negocio.
- `500 Internal Server Error`: error no controlado.

## 11. Como probar con Postman

1. Crear producto con `POST /api/productos`.
2. Listar con `GET /api/productos`.
3. Consultar por id con `GET /api/productos/{id}`.
4. Consultar por nombre con `GET /api/productos/nombre/{nombre}`.
5. Filtrar por categoria con `GET /api/productos/categoria/{categoria}`.
6. Actualizar con `PUT /api/productos/{id}`.
7. Eliminar logicamente con `DELETE /api/productos/{id}`.
8. Reintentar `GET /api/productos/{id}` para validar respuesta `404` cuando queda inactivo.

## 12. Como revisar datos en H2

En ejecución local, abrir la consola H2 definida por la aplicación (normalmente):

`http://localhost:8080/h2-console`

Tabla principal:

- `PRODUCTOS`

Consultas útiles:

```sql
SELECT * FROM PRODUCTOS;
SELECT * FROM PRODUCTOS WHERE LOWER(CATEGORIA) = LOWER('ETIQUETAS');
SELECT * FROM PRODUCTOS WHERE STOCK < 100;
```

## 13. Conexion futura con `pedido-service`

A futuro, al separar a microservicios, `pedido-service` podra validar `productoId` llamando al servicio
de productos por HTTP/mensajeria antes de confirmar un pedido. Tambien podra consultar precio o
metadatos de producto segun reglas de negocio vigentes.

## 14. Por que producto esta desacoplado del modulo pedido

Actualmente no existe relacion JPA entre `Pedido` y `Producto` para evitar acoplamiento entre dominios.
`Pedido` mantiene `productoId` y `nombreProducto` como snapshot historico del item. Esto permite que
cambios futuros en catalogo no alteren pedidos antiguos y facilita separar ambos modulos en servicios
independientes.

