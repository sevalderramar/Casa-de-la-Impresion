# Matriz de Requerimientos

Esta matriz resume requerimientos funcionales y no funcionales implementados para Casa de la Impresion. El proyecto corresponde a una entrega individual de Sebastian Valderrama.

| ID | Requerimiento declarado por el equipo | Tipo | Estado | Endpoint o evidencia | Prueba asociada |
|---|---|---|---|---|---|
| RF-01 | Crear pedido asociado a cliente y productos | Funcional | Implementado | `POST /api/pedidos`, `https://pedido-service-47kn.onrender.com/api/pedidos` | `PedidoControllerTest`, `PedidoServiceTest`, prueba REST Render |
| RF-02 | Listar pedidos registrados | Funcional | Implementado | `GET /api/pedidos`, Gateway `/api/pedidos` | `PedidoControllerTest`, Gateway Render validado |
| RF-03 | Consultar pedido por numero | Funcional | Implementado | `GET /api/pedidos/{numeroPedido}` y `/numero/{numeroPedido}` | `PedidoControllerTest`, `PedidoServiceTest` |
| RF-04 | Consultar pedidos por cliente | Funcional | Implementado | `GET /api/pedidos/cliente/{clienteId}` | `PedidoControllerTest`, `PedidoServiceTest` |
| RF-05 | Cambiar estado de pedido | Funcional | Implementado | `POST /api/pedidos/{numeroPedido}/estado`, `POST /api/estados` | `PedidoControllerTest`, `EstadoControllerTest`, `EstadoServiceTest` |
| RF-06 | Consultar historial de estado | Funcional | Implementado | `GET /api/estados/pedido/{numeroPedido}` | `EstadoControllerTest`, `docs/pruebas-rest/casa-de-la-impresion.http` |
| RF-07 | Gestion de clientes | Funcional | Implementado | `POST/GET/PUT/DELETE /api/clientes`, `https://cliente-service-6yfy.onrender.com/api/clientes` | `ClienteControllerTest`, `ClienteServiceTest`, Gateway Render validado |
| RF-08 | Gestion de productos | Funcional | Implementado | `POST/GET/PUT/DELETE /api/productos`, `https://producto-service-ulv6.onrender.com/api/productos` | `ProductoControllerTest`, `ProductoServiceTest`, Gateway Render validado |
| RF-09 | Gestion de despacho | Funcional | Implementado local | `POST/GET/PUT /api/despachos` | `DespachoControllerTest`, `DespachoServiceTest`, Swagger local |
| RF-10 | Gestion de fabricacion | Funcional | Implementado local | `POST /api/fabricacion`, `PATCH /api/fabricacion/{id}/estado` | `OrdenFabricacionControllerTest`, `OrdenFabricacionServiceTest` |
| RF-11 | Metricas de clientes, productos y ventas | Funcional | Implementado local | `/api/metricas/clientes/{id}`, `/ranking`, `/productos/top`, `/ventas` | `MetricaControllerTest`, `MetricaServiceImplTest` |
| RF-12 | Gestion de transportistas | Funcional | Implementado local | `POST/GET/PUT /api/transportistas` | `TransportistaControllerTest`, `TransportistaServiceImplTest` |
| RF-13 | Registro y consulta de logs | Funcional | Implementado local | `POST /api/logs`, `GET /api/logs` | `LogControllerTest`, `LogServiceImplTest` |
| RF-14 | Autenticacion y gestion de usuarios | Funcional | Implementado local | `POST /api/auth/login`, `/logout`, `/ping`, `/api/auth/usuarios` | `AuthControllerTest`, `UsuarioControllerTest`, `AuthServiceImplTest` |
| RNF-01 | Microservicios REST Spring Boot | No funcional | Implementado | 10 microservicios de dominio con controllers REST | Builds Maven y tests por servicio |
| RNF-02 | API Gateway centralizado | No funcional | Implementado | `api-gateway`, rutas `/api/**`, `https://api-gateway-c9qz.onrender.com` | Gateway health y rutas Render validadas |
| RNF-03 | Service Discovery Eureka | No funcional | Implementado | `discovery-server`, clientes Eureka, rutas `lb://` | `https://discovery-server-gjd0.onrender.com/eureka/apps` |
| RNF-04 | Swagger/OpenAPI | No funcional | Implementado | `/swagger-ui/index.html`, `/v3/api-docs` en microservicios | Swagger cliente/producto/estado/pedido en Render y Swagger local restantes |
| RNF-05 | Pruebas unitarias | No funcional | Implementado | 452 tests documentados | Surefire, JUnit 5, Mockito |
| RNF-06 | Cobertura mayor a 80% | No funcional | Implementado | JaCoCo en 10 microservicios | Reportes `target/site/jacoco/index.html` |
| RNF-07 | Docker Compose local | No funcional | Implementado parcial | `docker-compose.yml` con discovery, gateway, cliente, producto, pedido y estado | `docker compose build/up`, endpoints 200 documentados |
| RNF-08 | Despliegue Render remoto | No funcional | Implementado para flujo principal | Discovery, Gateway, cliente, producto, estado y pedido desplegados | `docs/render-deploy.md`, URLs Render, Gateway validado |
| RNF-09 | Manejo de errores | No funcional | Implementado | Handlers, validaciones DTO, errores Feign | Tests de controllers/services y casos invalidos `.http` |
| RNF-10 | Interaccion entre servicios | No funcional | Implementado | `pedido-service` integra cliente, producto y estado; Gateway usa Eureka | `CLIENTE_SERVICE_URL`, `PRODUCTO_SERVICE_URL`, `ESTADO_SERVICE_URL`, pruebas REST |
| RNF-11 | Seguridad de secretos | No funcional | Implementado | `.env.example`, `jwt.secret=${JWT_SECRET}` | Busqueda sin secretos reales; placeholders documentados |
| RNF-12 | Documentacion de entrega | No funcional | Implementado | README, docs tecnica/funcional/matriz/render/defensa | Archivos bajo `docs/` |
| RNF-13 | Evidencias de screenshots | No funcional | Pendiente de adjuntar | `docs/evidencias/` preparado | README de evidencias con lista esperada |
