# Levantamiento De Requerimientos Actualizado

Este documento registra como evolucionaron los requerimientos durante el semestre y que evidencia queda en el repositorio.

| ID | Requerimiento original | Cambio realizado | Justificacion | Estado final | Evidencia en repositorio |
|---|---|---|---|---|---|
| LR-01 | Gestionar pedidos | Se consolido `pedido-service` como servicio principal | Es el nucleo del negocio de Casa de la Impresion | Implementado | `pedido-service`, tests, Swagger |
| LR-02 | Registrar clientes | Se mantuvo como microservicio independiente | Separacion de datos de cliente y reutilizacion por pedidos | Implementado | `cliente-service`, `ClienteControllerTest` |
| LR-03 | Registrar productos | Se mantuvo como microservicio independiente | Catalogo reutilizable para pedidos y metricas | Implementado | `producto-service`, `ProductoServiceTest` |
| LR-04 | Cambiar estado de pedidos | Se agrego historial y consultas especificas | La trazabilidad de estado era necesaria para seguimiento | Implementado | `estado-service`, `GET /api/estados/pedido/{numeroPedido}` |
| LR-05 | Gestionar despacho | Se agrego como apoyo posterior al pedido | Cubre entrega, tipo de despacho y seguimiento | Implementado | `despacho-service` |
| LR-06 | Gestionar fabricacion | Se agrego para representar proceso productivo | El negocio requiere ordenes de fabricacion antes de despacho | Implementado | `fabricacion-service` |
| LR-07 | Obtener metricas | Se agregaron metricas de clientes, productos y ventas | Apoya decisiones y defensa de valor del sistema | Implementado | `metrica-service` |
| LR-08 | Gestionar transportistas | Se agrego para complementar despacho | Permite administrar operadores logisticos | Implementado | `transportista-service` |
| LR-09 | Registrar logs | Se agrego auditoria operacional | Mejora trazabilidad entre servicios | Implementado | `log-service` |
| LR-10 | Autenticacion | Se implemento JWT y usuarios | Aporta seguridad base y autenticacion centralizada | Implementado | `auth-service`, filtros JWT, tests |
| LR-11 | API Gateway | Se agrego Gateway central | Permite entrada unica y rutas unificadas | Implementado | `api-gateway/application.yml` |
| LR-12 | Service discovery | Se agrego `discovery-server` con Eureka en etapa final | Cumplir el entregable tecnico de discovery real y permitir enrutamiento Gateway por servicios registrados | Agregado/Implementado | `discovery-server`, clientes Eureka en microservicios y Gateway con rutas `lb://` |
| LR-13 | Documentacion Swagger | Se amplio a los 10 microservicios | Facilita evaluacion de endpoints y contrato API | Implementado | `OpenApiConfig`, `@Operation`, `/v3/api-docs` |
| LR-14 | Pruebas unitarias | Se amplio desde cobertura parcial a suite completa | Se corrigio feedback de baja cobertura | Implementado | 452 tests pasando |
| LR-15 | Cobertura JaCoCo | Se agrego JaCoCo en los 10 microservicios | Evidencia objetiva para entrega final | Implementado | Reportes `target/site/jacoco` |
| LR-16 | Docker | Se mantuvo como demo minima del flujo principal | Permite levantar flujo esencial sin todos los modulos | Parcial | `docker-compose.yml` incluye Gateway, pedido, cliente, producto, estado |
| LR-17 | Render | Se postergo despliegue real y se documenta configuracion | No existen URLs publicas reales verificadas | Pendiente/configurable | `docs/render-deploy.md` |
| LR-18 | Documentacion formal final | Se crea carpeta `docs/` con entregables | La pauta exige documentos separados para cierre | Implementado en documentacion | Archivos bajo `docs/` |
| LR-19 | Coleccion REST | Se crea archivo `.http` | Permite demostrar endpoints sin Postman obligatorio | Implementado en documentacion | `docs/pruebas-rest/casa-de-la-impresion.http` |
| LR-20 | No exponer credenciales | Se usa `.env.example` con placeholders | Seguridad y buenas practicas | Implementado | `.env.example`, README |
