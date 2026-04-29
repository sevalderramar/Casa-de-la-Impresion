# Módulo `cliente` — Documentación técnica

Este documento describe en detalle el módulo `cliente` de la aplicación `sistema-gestion-pedidos`.
Está dirigido a desarrolladores e ingenieros que mantendrán o integrarán con el módulo, y explica
su objetivo, estructura, entidades, reglas de negocio y endpoints REST.

---

1. Objetivo del módulo cliente
------------------------------

El módulo `cliente` gestiona la vida de los clientes dentro del sistema: creación, consulta,
actualización y eliminación lógica. Provee una API REST para exponer operaciones sobre clientes y
mantiene las reglas de negocio necesarias para asegurar la integridad de los datos.

2. Qué problema resuelve
------------------------

Centraliza la gestión de datos maestros de clientes, evitando duplicidad y proporcionando
validaciones (por ejemplo RUT único, formato de email y teléfono), auditabilidad (fecha de registro)
y control de estado (clientes activos/inactivos). Facilita además la integración con otros módulos
como `pedido` que necesitan referenciar clientes por `clienteId`.

3. Estructura del módulo
------------------------

El módulo está organizado por responsabilidades internas, siguiendo la convención:

- `controller` — Exposición REST y manejo de peticiones HTTP.
- `dto` — Objetos de transferencia (requests/responses) usados por los controladores.
- `model` — Entidades persistentes (JPA/Hibernate) que representan la tabla de clientes.
- `repository` — Interfaces de acceso a datos (Spring Data JPA) para `Cliente`.
- `service` — Lógica de negocio, validaciones y operaciones transaccionales.

4. Qué hace cada clase del módulo (visión general)
-------------------------------------------------

Nota: los nombres de clase citados corresponden a los archivos presentes en el proyecto bajo
`cl.duocuc.sistemagestionpedidos.cliente`.

- `controller/ClienteController.java`: expone los endpoints REST para crear, leer, actualizar y
  eliminar (lógicamente) clientes. Valida entradas básicas y delega la lógica al `ClienteService`.
- `dto/ClienteRequest.java`: DTO recibido en las peticiones de creación o actualización de cliente.
- `dto/ClienteResponse.java`: DTO devuelto en las respuestas con los datos del cliente.
- `model/Cliente.java`: entidad JPA que representa la tabla `CLIENTES`. Incluye atributos como
  `nombre`, `rut`, `email`, `telefono`, `direccion`, `comuna`, `region`, `fechaRegistro`, `estado`.
- `repository/ClienteRepository.java`: interfaz Spring Data para consultas y persistencia de `Cliente`.
- `service/ClienteService.java`: contiene las reglas de negocio, validaciones y operaciones atómicas
  (crear, actualizar, borrar lógicamente, buscar por rut, etc.).

5. Entidad `Cliente` y sus atributos
----------------------------------

La entidad `Cliente` representa los datos maestros de un cliente y contiene los siguientes atributos
clave:

- `id` (Long): identificador único interno (clave primaria).
- `nombre` (String): nombre completo del cliente.
- `rut` (String): RUT chileno del cliente; campo único en la base de datos.
- `email` (String): correo electrónico de contacto.
- `telefono` (String): teléfono de contacto.
- `direccion` (String): dirección física.
- `comuna` (String): comuna de residencia o despacho.
- `region` (String): región administrativa.
- `fechaRegistro` (LocalDateTime / Date): marca temporal de creación del registro.
- `estado` (String / Enum): estado operativo del cliente; valores típicos: `ACTIVO`, `INACTIVO`.

Nota: en este proyecto el campo `estado` reemplaza la nomenclatura `activo` para facilitar extensiones
futuras (más estados posibles si se requiere).

6. Por qué almacenar `clienteId` en pedidos en lugar de una relación fuerte
-----------------------------------------------------------------------

El diseño desacoplado entre módulos previene dependencias directas entre entidades de diferentes
dominios. El `pedido` almacena `clienteId` en vez de mapear una relación JPA a `Cliente` por las
sigientes razones:

- Facilita la futura migración a microservicios: el módulo `cliente` puede convertirse en servicio
  independiente sin romper el esquema de `pedido`.
- Evita cargas y operaciones en cascada no deseadas entre dominios.
- Reduce acoplamiento y mejora independencia de despliegue y escalabilidad.

7. Validaciones que aún no están descentralizadas
------------------------------------------------

Actualmente el módulo `cliente` funciona localmente y no depende de otros servicios. En futuros
despliegues distribuidos se debe asegurar coordinación entre servicios (ej. `pedido` validando
existencia de `clienteId` en `cliente-service`).

8. Cálculos y comportamientos automáticos
----------------------------------------

Aunque la entidad `Cliente` no contiene cálculos numéricos como `Pedido`, el módulo aplica ciertos
comportamientos automáticos:

- `fechaRegistro` se asigna automáticamente al crear un cliente si no es provista.
- `estado` por defecto se inicializa como `ACTIVO` al crear un cliente.

9. Eliminación lógica
----------------------

La eliminación de clientes es lógica: se marca el `estado` como `INACTIVO` en lugar de borrar la fila.
Esto preserva el histórico y permite auditoría. Las consultas por defecto deben devolver únicamente
clientes con `estado = ACTIVO` salvo que se requiera lo contrario.

10. DTOs
--------

- `ClienteRequest`:
  - DTO para crear o actualizar un cliente.
  - Campos típicos: `nombre`, `rut`, `email`, `telefono`, `direccion`, `comuna`, `region`.
  - No incluye `id`, `fechaRegistro` ni `estado` (estos son gestionados por el servidor).

- `ClienteResponse`:
  - DTO devuelto al consumidor con todos los datos persistidos del cliente.
  - Incluye `id`, `nombre`, `rut`, `email`, `telefono`, `direccion`, `comuna`, `region`, `fechaRegistro`,
    `estado`.

11. Repository
--------------

`ClienteRepository` extiende típicamente `JpaRepository<Cliente, Long>` y provee:

- Operaciones CRUD estándar.
- Consultas derivadas por nombre (por ejemplo `findByRutAndEstado` o `findByEstado`).
- Métodos para verificar unicidad (`existsByRut`) y búsquedas paginadas.

12. Service y reglas de negocio
-------------------------------

`ClienteService` centraliza la lógica del dominio. Principales responsabilidades y validaciones:

- Verificar que `rut` sea único al crear un nuevo cliente; si existe conflicto lanzar `ConflictException`.
- Validar formato de `rut` (si aplica) y normalizarlo para almacenamiento.
- Validar formato de `email` y, opcionalmente, verificar dominio y sintaxis.
- Validar que `telefono` cumpla formatos mínimos (longitud y caracteres permitidos) si se requiere.
- Asignar `fechaRegistro` al crear si no está presente.
- Establecer `estado = ACTIVO` por defecto y soportar cambios de `estado` mediante actualización.
- Implementar eliminación lógica asignando `estado = INACTIVO`.

13. Controller y endpoints
--------------------------

El controlador `ClienteController` expone los siguientes endpoints REST (rutas basadas en
`/api/clientes`):

- POST /api/clientes
  - Crea un nuevo cliente a partir de un `ClienteRequest`.
  - Valida reglas de negocio y devuelve `201 Created` con `ClienteResponse`.

- GET /api/clientes
  - Lista clientes (por defecto `estado = ACTIVO`). Soporta paginación y filtros simples.

- GET /api/clientes/{id}
  - Obtiene un cliente por su `id` y devuelve `ClienteResponse`.

- GET /api/clientes/rut/{rut}
  - Busca un cliente por su RUT.

- PUT /api/clientes/{id}
  - Actualiza los datos del cliente (reemplazo completo o parcial según implementación).

- DELETE /api/clientes/{id}
  - Realiza eliminación lógica: establece `estado = INACTIVO`.

14. Ejemplos JSON para Postman
-----------------------------

- POST crear cliente (ejemplo válido):

```json
{
  "nombre": "Juan Perez",
  "rut": "12345678-9",
  "email": "juan@gmail.com",
  "telefono": "999999999",
  "direccion": "Av Siempre Viva 123",
  "comuna": "Santiago",
  "region": "Metropolitana"
}
```

Respuesta esperada: `201 Created` con JSON equivalente a `ClienteResponse` incluyendo `id`,
`fechaRegistro` y `estado`.

15. Cómo revisar datos en H2
---------------------------

Durante ejecución local la aplicación usa la base de datos en memoria H2. La consola web suele estar
disponible en `http://localhost:8080/h2-console` (ver `application.properties` para confirmar URL).

Tabla relevante (nombres según mapeo JPA):

- `CLIENTES` — tabla que almacena la entidad `Cliente`.

Ejemplos de consultas SQL en la consola H2:

```sql
SELECT * FROM CLIENTES WHERE ESTADO = 'ACTIVO';
SELECT * FROM CLIENTES WHERE RUT = '12345678-9';
```

16. Próximos pasos (mejoras y evolución)
---------------------------------------

Recomendaciones para evolución del módulo:

- Implementar normalización y validación robusta del `rut` (ver bibliotecas locales para RUT chileno).
- Añadir verificaciones de correo electrónico (opcional: email verification por token).
- Integrar con un servicio de identidad / usuarios si se requiere autenticación por cliente.
- Añadir métricas (Prometheus / Micrometer) para monitorear creación, actualización y eliminación de
  clientes.
- Publicar eventos de dominio (por ejemplo `ClienteCreado`, `ClienteActualizado`) para sincronización
  con otros módulos/microservicios (p. ej. `pedido`) sin acoplamiento directo.

---

Anexos y notas:

- Mantener los DTOs y contratos REST estables facilita la migración a microservicios.
- Documentar enumeraciones y validaciones en el código y en la documentación API (OpenAPI/Swagger).

Fin del documento.

