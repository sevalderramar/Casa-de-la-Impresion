# Levantamiento De Requerimientos Actualizado

Este documento registra como evolucionaron los requerimientos durante el semestre y que evidencia queda en el repositorio. El proyecto fue desarrollado individualmente por Sebastian Valderrama.

| ID | Requerimiento original | Cambio realizado | Justificacion | Estado final | Evidencia en repositorio |
|---|---|---|---|---|---|
| LR-01 | Gestionar pedidos | Se consolido `pedido-service` como servicio principal | Es el nucleo del negocio | Implementado | `pedido-service`, `PedidoControllerTest`, Swagger pedido, `https://pedido-service-47kn.onrender.com/api/pedidos` |
| LR-02 | Registrar clientes | Se mantuvo como microservicio independiente | Separacion de datos de cliente y reutilizacion por pedidos | Implementado | `cliente-service`, `ClienteControllerTest`, Swagger cliente, `https://cliente-service-6yfy.onrender.com/api/clientes` |
| LR-03 | Registrar productos | Se mantuvo como microservicio independiente | Catalogo reutilizable para pedidos y metricas | Implementado | `producto-service`, `ProductoServiceTest`, Swagger producto, `https://producto-service-ulv6.onrender.com/api/productos` |
| LR-04 | Cambiar estado de pedidos | Se agrego historial y consultas especificas | Trazabilidad de estado para seguimiento | Implementado | `estado-service`, `/api/estados`, `https://estado-service.onrender.com/actuator/health` |
| LR-05 | Gestionar despacho | Se agrego como apoyo posterior al pedido | Cubre entrega, tipo de despacho y seguimiento | Implementado local | `despacho-service`, `DespachoControllerTest`, Swagger local |
| LR-06 | Gestionar fabricacion | Se agrego para representar proceso productivo | El negocio requiere ordenes de fabricacion antes de despacho | Implementado local | `fabricacion-service`, tests y Swagger local |
| LR-07 | Obtener metricas | Se agregaron metricas de clientes, productos y ventas | Apoya decisiones y defensa de valor del sistema | Implementado local | `metrica-service`, tests y Swagger local |
| LR-08 | Gestionar transportistas | Se agrego para complementar despacho | Permite administrar operadores logisticos | Implementado local | `transportista-service`, tests y Swagger local |
| LR-09 | Registrar logs | Se agrego auditoria operacional | Mejora trazabilidad entre servicios | Implementado local | `log-service`, tests y Swagger local |
| LR-10 | Autenticacion | Se implemento JWT y usuarios | Aporta seguridad base y autenticacion centralizada | Implementado local | `auth-service`, filtros JWT, tests, Swagger local |
| LR-11 | API Gateway | Se agrego Gateway central | Permite entrada unica y rutas unificadas | Implementado y validado remoto | `api-gateway/application.yml`, `https://api-gateway-c9qz.onrender.com/api/clientes`, `/api/productos`, `/api/pedidos` |
| LR-12 | Service discovery | Se agrego `discovery-server` con Eureka | Cumplir discovery real y permitir enrutamiento `lb://` | Implementado y validado remoto | `discovery-server`, Eureka apps `https://discovery-server-gjd0.onrender.com/eureka/apps` |
| LR-13 | Documentacion Swagger | Se amplio a los 10 microservicios | Facilita evaluacion de endpoints y contrato API | Implementado | `OpenApiConfig`, `@Operation`, `/v3/api-docs`, Swagger Render cliente/producto/estado/pedido |
| LR-14 | Pruebas unitarias | Se amplio desde cobertura parcial a suite completa | Se corrigio feedback de baja cobertura | Implementado | 452 tests documentados, JUnit y Mockito |
| LR-15 | Cobertura JaCoCo | Se agrego JaCoCo en los 10 microservicios | Evidencia objetiva para entrega final | Implementado | Reportes `target/site/jacoco`, todos sobre 80% |
| LR-16 | Docker | Se mantuvo como demo minima del flujo principal | Permite levantar flujo esencial sin todos los modulos | Implementado parcialmente | `docker-compose.yml` incluye discovery, gateway, cliente, producto, pedido y estado |
| LR-17 | Render | Se desplego flujo principal de validacion tecnica | Permite demostrar Eureka, Gateway y servicios remotos | Implementado para flujo principal | `README.md`, `docs/render-deploy.md`, `.http`, URLs Render reales |
| LR-18 | Documentacion formal final | Se crea carpeta `docs/` con entregables | La pauta exige documentos separados para cierre | Implementado | `docs/documentacion-tecnica.md`, `docs/documentacion-funcional.md`, matriz y defensa |
| LR-19 | Coleccion REST | Se crea archivo `.http` local y remoto | Permite demostrar endpoints sin Postman obligatorio | Implementado | `docs/pruebas-rest/casa-de-la-impresion.http` con variables Render |
| LR-20 | No exponer credenciales | Se usa `.env.example` con placeholders | Seguridad y buenas practicas | Implementado | `.env.example`, README, busqueda de secretos sin valores reales |
| LR-21 | Evidencias de entrega | Se incorporan capturas reales en las carpetas de evidencias | Render Dashboard, Eureka Discovery, Swagger/OpenAPI y API Gateway tienen capturas reales | Implementado | `docs/evidencias/render/`, `docs/evidencias/eureka/`, `docs/evidencias/swagger/`, `docs/evidencias/gateway/` |

## Alcance Render Final

Implementado para flujo principal de validacion tecnica: `discovery-server`, `api-gateway`, `cliente-service`, `producto-service`, `estado-service` y `pedido-service`. Servicios complementarios quedan implementados localmente y documentados, pero no forman parte del despliegue remoto principal validado.
