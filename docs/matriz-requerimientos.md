# Matriz de Requerimientos

Esta matriz resume el cumplimiento funcional y tecnico del proyecto academico Casa de la Impresion. El proyecto se mantiene como entrega individual de Sebastian Valderrama.

## Requerimientos Funcionales

| ID | Requerimiento | Estado | Evidencia | Pruebas |
|---|---|---|---|---|
| RF-01 | Crear pedido asociado a cliente y productos | Implementado | `POST /api/pedidos` en `pedido-service` y Gateway | `PedidoControllerTest`, `PedidoServiceTest` |
| RF-02 | Listar pedidos registrados | Implementado | `GET /api/pedidos` | `PedidoControllerTest` |
| RF-03 | Consultar pedido por numero | Implementado | `GET /api/pedidos/{numeroPedido}` y `/numero/{numeroPedido}` | `PedidoControllerTest`, `PedidoServiceTest` |
| RF-04 | Consultar pedidos por cliente | Implementado | `GET /api/pedidos/cliente/{clienteId}` | `PedidoControllerTest`, `PedidoServiceTest` |
| RF-05 | Cambiar estado de pedido | Implementado | `POST /api/pedidos/{numeroPedido}/estado`, `POST /api/estados` | `PedidoControllerTest`, `EstadoControllerTest` |
| RF-06 | Consultar historial de estado | Implementado | `GET /api/pedidos/{numeroPedido}/historial`, `GET /api/estados/pedido/{numeroPedido}` | `PedidoControllerTest`, `EstadoControllerTest` |
| RF-07 | Gestion de clientes | Implementado | `POST/GET/PUT/DELETE /api/clientes` | `ClienteControllerTest`, `ClienteServiceTest` |
| RF-08 | Gestion de productos | Implementado | `POST/GET/PUT/DELETE /api/productos` | `ProductoControllerTest`, `ProductoServiceTest` |
| RF-09 | Gestion de despacho | Implementado | `POST/GET/PUT /api/despachos` | `DespachoControllerTest`, `DespachoServiceTest` |
| RF-10 | Gestion de fabricacion | Implementado | `POST /api/fabricacion`, `PATCH /api/fabricacion/{id}/estado` | `OrdenFabricacionControllerTest`, `OrdenFabricacionServiceTest` |
| RF-11 | Metricas de clientes, productos y ventas | Implementado | `/api/metricas/clientes/{id}`, `/ranking`, `/productos/top`, `/ventas` | `MetricaControllerTest`, `MetricaServiceImplTest` |
| RF-12 | Gestion de transportistas | Implementado | `POST/GET/PUT /api/transportistas` | `TransportistaControllerTest`, `TransportistaServiceImplTest` |
| RF-13 | Registro y consulta de logs | Implementado | `POST /api/logs`, `GET /api/logs` | `LogControllerTest`, `LogServiceImplTest` |
| RF-14 | Autenticacion y gestion de usuarios | Implementado | `POST /api/auth/login`, `/logout`, `/ping`, `/api/auth/usuarios` | `AuthControllerTest`, `UsuarioControllerTest`, `AuthServiceImplTest` |

## Requerimientos Tecnicos Y No Funcionales

| ID | Requerimiento | Estado | Evidencia | Observacion |
|---|---|---|---|---|
| RNF-01 | Microservicios REST Spring Boot | Implementado | 10 microservicios de dominio con controllers REST | Separacion por responsabilidad |
| RNF-02 | API Gateway | Implementado | `api-gateway`, rutas `/api/**`, Render `https://api-gateway-c9qz.onrender.com` | Enrutamiento centralizado |
| RNF-03 | Service Discovery Eureka | Implementado | `discovery-server`, clientes Eureka, rutas `lb://` | Eureka local y Render validado |
| RNF-04 | Swagger/OpenAPI | Implementado | `/swagger-ui/index.html`, `/v3/api-docs` en microservicios | Gateway no expone Swagger propio |
| RNF-05 | Pruebas unitarias | Implementado | 452 tests documentados | JUnit 5 y Mockito |
| RNF-06 | Cobertura mayor a 80% | Implementado | JaCoCo en 10 microservicios | Todos sobre 80% line coverage |
| RNF-07 | Docker/local | Implementado parcial | `docker-compose.yml` con discovery, gateway, cliente, producto, pedido y estado | Demo minima del flujo principal |
| RNF-08 | Render remoto | Implementado parcial validado | discovery, gateway, cliente, producto, estado y pedido desplegados | Servicios complementarios pendientes |
| RNF-09 | Documentacion | Implementado | `README.md`, `docs/render-deploy.md`, `docs/documentacion-tecnica.md` | Incluye validacion tecnica y evidencias |
| RNF-10 | GitHub | Implementado | `https://github.com/sevalderramar/Casa-de-la-Impresion` | Rama `main` como base |
| RNF-11 | Manejo de errores | Implementado | Handlers, validaciones DTO, pruebas de errores remotos | Incluye escenarios Feign |
| RNF-12 | Interaccion entre servicios | Implementado | `pedido-service` integra cliente, producto y estado | Feign por variables URL; Gateway por Eureka |
| RNF-13 | Seguridad de secretos | Implementado | `.env.example` con placeholders, `jwt.secret=${JWT_SECRET}` | No versionar secretos reales |

## Endpoints Render Validados

| Evidencia | URL |
|---|---|
| Eureka apps | `https://discovery-server-gjd0.onrender.com/eureka/apps` |
| Gateway health | `https://api-gateway-c9qz.onrender.com/actuator/health` |
| Gateway clientes | `https://api-gateway-c9qz.onrender.com/api/clientes` |
| Gateway productos | `https://api-gateway-c9qz.onrender.com/api/productos` |
| Gateway pedidos | `https://api-gateway-c9qz.onrender.com/api/pedidos` |
| Swagger cliente | `https://cliente-service-6yfy.onrender.com/swagger-ui/index.html` |
| Swagger producto | `https://producto-service-ulv6.onrender.com/swagger-ui/index.html` |
| Swagger estado | `https://estado-service.onrender.com/swagger-ui/index.html` |
| Swagger pedido | `https://pedido-service-47kn.onrender.com/swagger-ui/index.html` |

Nota: en Render Free los servicios pueden dormir; antes de validar Gateway se deben despertar con `/actuator/health` y revisar Eureka.
