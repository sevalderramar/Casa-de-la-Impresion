# Matriz de Requerimientos

Esta matriz resume los requerimientos reales implementados y documentados para Casa de la Impresion. El servicio principal del dominio es `pedido-service`; los demas microservicios apoyan autenticacion, catalogo, clientes, estados, despacho, fabricacion, metricas, transportistas y logs.

| ID | Requerimiento declarado por el estudiante | Tipo | Estado | Endpoint o evidencia | Prueba asociada |
|---|---|---|---|---|---|
| RF-01 | Crear pedido asociado a cliente y productos | Funcional | Implementado | `POST /api/pedidos` en `pedido-service` | `PedidoControllerTest`, `PedidoServiceTest` |
| RF-02 | Listar pedidos registrados | Funcional | Implementado | `GET /api/pedidos` | `PedidoControllerTest` |
| RF-03 | Consultar pedido por numero | Funcional | Implementado | `GET /api/pedidos/{numeroPedido}` y `GET /api/pedidos/numero/{numeroPedido}` | `PedidoControllerTest`, `PedidoServiceTest` |
| RF-04 | Consultar pedidos por cliente | Funcional | Implementado | `GET /api/pedidos/cliente/{clienteId}` | `PedidoControllerTest`, `PedidoServiceTest` |
| RF-05 | Cambiar estado de pedido | Funcional | Implementado | `POST /api/pedidos/{numeroPedido}/estado` y `POST /api/estados` | `PedidoControllerTest`, `EstadoControllerTest`, `EstadoServiceTest` |
| RF-06 | Consultar historial de estado | Funcional | Implementado | `GET /api/pedidos/{numeroPedido}/historial`, `GET /api/estados/pedido/{numeroPedido}` | `PedidoControllerTest`, `EstadoControllerTest` |
| RF-07 | Gestion de clientes | Funcional | Implementado | `POST/GET/PUT/DELETE /api/clientes`, `GET /api/clientes/rut/{rut}` | `ClienteControllerTest`, `ClienteServiceTest` |
| RF-08 | Gestion de productos | Funcional | Implementado | `POST/GET/PUT/DELETE /api/productos`, filtros por nombre y categoria | `ProductoControllerTest`, `ProductoServiceTest` |
| RF-09 | Gestion de despacho | Funcional | Implementado | `POST/GET/PUT /api/despachos` | `DespachoControllerTest`, `DespachoServiceTest` |
| RF-10 | Gestion de fabricacion | Funcional | Implementado | `POST /api/fabricacion`, `PATCH /api/fabricacion/{id}/estado` | `OrdenFabricacionControllerTest`, `OrdenFabricacionServiceTest` |
| RF-11 | Metricas de clientes, productos y ventas | Funcional | Implementado | `GET /api/metricas/clientes/{id}`, `/ranking`, `/productos/top`, `/ventas` | `MetricaControllerTest`, `MetricaServiceImplTest` |
| RF-12 | Gestion de transportistas | Funcional | Implementado | `POST/GET/PUT /api/transportistas` | `TransportistaControllerTest`, `TransportistaServiceImplTest` |
| RF-13 | Registro y consulta de logs | Funcional | Implementado | `POST /api/logs`, `GET /api/logs` | `LogControllerTest`, `LogServiceImplTest` |
| RF-14 | Autenticacion y gestion de usuarios | Funcional | Implementado | `POST /api/auth/login`, `/logout`, `/ping`, `/api/auth/usuarios` | `AuthControllerTest`, `UsuarioControllerTest`, `AuthServiceImplTest` |
| RNF-01 | Exponer Gateway centralizado | No funcional | Implementado | `api-gateway` en `8080`, rutas `/api/**` | `README.md`, `application.yml`, build `api-gateway` |
| RNF-02 | Documentar APIs con Swagger/OpenAPI | No funcional | Implementado | 10 microservicios con `/swagger-ui/index.html` y `/v3/api-docs` | `OpenApiConfig`, anotaciones `@Operation`, `@Tag` |
| RNF-03 | Mantener pruebas unitarias automatizadas | No funcional | Implementado | 452 tests pasando | Reportes Surefire y README final |
| RNF-04 | Medir cobertura con JaCoCo | No funcional | Implementado | 10 microservicios con JaCoCo y mas de 80% de lineas | `target/site/jacoco/index.html` por servicio |
| RNF-05 | No exponer credenciales reales | No funcional | Implementado | `.env.example` con placeholders | Revision de repositorio y README de seguridad |
| RNF-06 | Facilitar ejecucion local | No funcional | Implementado | Perfiles `h2`, `application-h2.properties`, Maven Wrapper | `README.md`, builds `clean verify` |
| RNF-07 | Facilitar demo Docker | No funcional | Parcial | `docker-compose.yml` para Gateway, pedido, cliente, producto y estado | `docker compose build/up`, README indica demo minima |
| RNF-08 | Discovery o mecanismo equivalente | No funcional | Parcial documentado | Discovery actual por rutas estaticas y variables env en Gateway, sin Eureka real | `api-gateway/application.yml`, `README.md` |
| RNF-09 | Despliegue Render | No funcional | Pendiente/configurable | Documentacion con placeholders y variables en `docs/render-deploy.md` | Pendiente de URLs reales Render |
