# Defensa Individual - Sebastian Valderrama

## Rol

Rol principal: desarrollador unico y responsable integral del proyecto. Sebastian Valderrama realizo individualmente la arquitectura de microservicios, backend, API Gateway, pruebas automatizadas, documentacion Swagger/OpenAPI, documentacion final y cierre de feedback.

## Modulos Desarrollados Y Cerrados

| Modulo | Trabajo realizado |
|---|---|
| `auth-service` | Ampliacion de cobertura, Swagger y revision de seguridad JWT |
| `estado-service` | Cobertura, Swagger y documentacion de estados |
| `transportista-service` | Cobertura, Swagger y validacion de endpoints |
| `log-service` | Cobertura, Swagger y documentacion final |
| `metrica-service` | Cobertura y validacion de metricas |
| `fabricacion-service` | Cobertura y validacion de flujo productivo |
| `despacho-service` | Cobertura y validacion de despacho |
| Proyecto completo | Arquitectura, auditoria final, README y documentos de entrega |

## Commits Relevantes

| Hash | Commit | Aporte |
|---|---|---|
| `628f855` | `docs: actualizar resumen final de entrega` | README final con metricas, Swagger y evidencias |
| `abdf512` | `docs: documentar swagger log service` | Swagger/OpenAPI en `log-service` |
| `60a9bf3` | `docs: documentar swagger transportista service` | Swagger/OpenAPI en `transportista-service` |
| `1a7be11` | `docs: documentar swagger estado service` | Swagger/OpenAPI en `estado-service` |
| `938fae2` | `docs: documentar swagger auth service` | Swagger/OpenAPI en `auth-service` |
| `19bc033` | `test: ampliar cobertura log service` | Pruebas y JaCoCo para `log-service` |
| `dc48050` | `test: ampliar cobertura transportista service` | Pruebas y JaCoCo para `transportista-service` |
| `5c1ecd1` | `test: ampliar cobertura auth service` | Pruebas y JaCoCo para `auth-service` |
| `eced2f7` | `test: ampliar cobertura metrica service` | Pruebas y JaCoCo para `metrica-service` |
| `2734355` | `test: ampliar cobertura fabricacion service` | Pruebas y JaCoCo para `fabricacion-service` |
| `f39df2a` | `test: ampliar cobertura despacho service` | Pruebas y JaCoCo para `despacho-service` |
| `dee2aad` | `test: ampliar cobertura estado service` | Pruebas y JaCoCo para `estado-service` |

## Tareas Realizadas

| Tarea | Resultado |
|---|---|
| Ampliar pruebas unitarias | 452 tests pasando en auditoria final |
| Configurar o validar JaCoCo | 10 microservicios con cobertura de lineas sobre 80% |
| Documentar Swagger/OpenAPI | 10 microservicios con Swagger disponible |
| Auditar proyecto completo | Identificacion de pendientes documentales y de Render |
| Actualizar README final | Evidencias y comandos para defensa agregados |
| Crear documentacion formal | Carpeta `docs/` con entregables finales |

## Feedback Corregido

| Feedback | Correccion aplicada |
|---|---|
| Pocos servicios con tests | Se amplio cobertura a los 10 microservicios |
| Swagger incompleto | Se documento Swagger en auth, estado, transportista y log, y se audito el resto |
| Falta evidencia final | README y docs incluyen metricas, URLs y comandos |
| Falta cierre documental | Se generaron documentos obligatorios de entrega |

## Archivos Principales

| Archivo o carpeta | Uso |
|---|---|
| `README.md` | Resumen final de entrega |
| `docs/matriz-requerimientos.md` | Trazabilidad RF/RNF |
| `docs/documentacion-tecnica.md` | Arquitectura, ejecucion y despliegue |
| `docs/documentacion-funcional.md` | Actores, flujos y reglas |
| `docs/pruebas-rest/casa-de-la-impresion.http` | Pruebas manuales REST |
| `log-service/src/main/java/...` | Swagger y pruebas del servicio de logs |
| `transportista-service/src/main/java/...` | Swagger y pruebas de transportistas |
| `auth-service/src/main/java/...` | Swagger, JWT y usuarios |
| `estado-service/src/main/java/...` | Estados e historial |

## Endpoints Asociados Que Debe Dominar

| Servicio | Endpoints |
|---|---|
| `auth-service` | `POST /api/auth/login`, `POST /api/auth/logout`, `GET /api/auth/ping`, `/api/auth/usuarios` |
| `estado-service` | `POST /api/estados`, `GET /api/estados/pedido/{numeroPedido}`, `GET /api/estados/pedido/{numeroPedido}/ultimo` |
| `transportista-service` | `POST /api/transportistas`, `GET /api/transportistas`, `GET /api/transportistas/{id}`, `PUT /api/transportistas/{id}`, `GET /api/transportistas/ping` |
| `log-service` | `POST /api/logs`, `GET /api/logs`, `GET /api/logs/ping` |
| `metrica-service` | `/api/metricas/clientes/{id}`, `/ranking`, `/productos/top`, `/ventas` |

## Pruebas Asociadas

| Servicio | Pruebas relevantes |
|---|---|
| `auth-service` | `AuthControllerTest`, `UsuarioControllerTest`, `JwtAuthFilterTest`, `JwtUtilTest` |
| `estado-service` | `EstadoControllerTest`, `EstadoServiceTest`, `SecurityConfigTest` |
| `transportista-service` | `TransportistaControllerTest`, `TransportistaServiceImplTest` |
| `log-service` | `LogControllerTest`, `LogServiceImplTest`, `LogEntradaTest` |
| `metrica-service` | `MetricaControllerTest`, `MetricaServiceImplTest` |

## Regla De Negocio Que Domina

La trazabilidad de pedidos se sostiene con cambios de estado e historial. Cada cambio debe quedar consultable por numero de pedido y permitir identificar el ultimo estado. Esta regla conecta `pedido-service` con `estado-service` y permite explicar el ciclo completo del pedido.

## Relacion De Datos Que Domina

| Relacion | Explicacion |
|---|---|
| Pedido - Cliente | Un pedido referencia cliente existente |
| Pedido - Producto | Un pedido contiene productos y cantidades |
| Pedido - Estado | Un pedido tiene cambios de estado historicos |
| Pedido - Despacho | Un despacho se asocia a numero de pedido |
| Pedido - Fabricacion | Una orden de fabricacion representa avance productivo |
| Servicio - Log | Cada evento puede registrar servicio, operacion y resultado |

## Comunicacion Entre Servicios Que Domina

El sistema usa Gateway para entrada externa y OpenFeign/REST para llamadas internas. Se implemento `discovery-server` con Eureka Server en `http://localhost:8761`; los 10 microservicios y el `api-gateway` se registran como clientes Eureka. El Gateway enruta mediante URIs `lb://` y mantiene los prefijos `/api/**`.

## Aporte Personal En Discovery

Como responsable integral del proyecto, Sebastian Valderrama implemento el modulo `discovery-server`, registro los microservicios como clientes Eureka, actualizo el Gateway para enrutar con `lb://` y valido el flujo usando la consola Eureka y endpoints expuestos por Gateway.

## Dificultad Tecnica Y Solucion

| Dificultad | Solucion aplicada |
|---|---|
| Aumentar cobertura sin cambiar logica | Crear pruebas unitarias y de controller enfocadas en comportamiento real |
| Documentar Swagger sin alterar endpoints | Agregar anotaciones y `OpenApiConfig` sin cambiar rutas ni servicios |
| Incorporar discovery real | Agregar Eureka Server, registrar servicios y actualizar Gateway a `lb://` |
| Cerrar documentacion final | Crear documentos formales con estado real y pendientes claros |

## Checklist Personal

| Item | Estado |
|---|---|
| Conozco el flujo de pedido completo | Si |
| Puedo explicar Gateway y rutas | Si |
| Puedo mostrar consola Eureka y servicios registrados | Si |
| Puedo mostrar Swagger de servicios | Si |
| Puedo mostrar JaCoCo y tests | Si |
| Puedo explicar JWT y modo demo/local | Si |
| Puedo justificar Docker Compose minimo | Si |
| Puedo explicar Eureka real y Gateway `lb://` | Si |
| Puedo explicar pendientes de Render | Si |
