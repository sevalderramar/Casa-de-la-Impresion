# Plan De Cierre De Feedback

Este plan resume observaciones corregidas o pendientes antes de la entrega final.

| ID | Observacion o feedback recibido | Accion realizada | Archivo(s) modificados | Evidencia de verificacion | Estado |
|---|---|---|---|---|---|
| FB-01 | Tests solo en 3-4 servicios | Se ampliaron pruebas a los 10 microservicios de dominio | `src/test/java` en auth, cliente, producto, pedido, estado, despacho, fabricacion, metrica, transportista, log | 452 tests pasando | Cerrado |
| FB-02 | Cobertura insuficiente | Se agrego JaCoCo y se mejoraron pruebas reales | `pom.xml` de microservicios y tests asociados | 10 microservicios sobre 80% de cobertura de lineas | Cerrado |
| FB-03 | Swagger incompleto | Se completo Swagger/OpenAPI en los 10 microservicios | `OpenApiConfig`, controllers, DTOs/modelos | `/swagger-ui/index.html` y `/v3/api-docs` por servicio | Cerrado |
| FB-04 | Gateway con rutas limitadas | Gateway contiene rutas centralizadas para los servicios | `api-gateway/src/main/resources/application.yml` | Rutas `/api/auth/**`, `/api/pedidos/**`, etc. | Cerrado tecnico |
| FB-05 | Feign y errores remotos poco cubiertos | Se agregaron pruebas para clientes Feign y fallos remotos | Tests en pedido, metrica, fabricacion, despacho | Tests Feign y service pasando | Cerrado |
| FB-06 | Docker local incompleto | Se mantiene Docker Compose como demo minima y se documenta alcance | `docker-compose.yml`, README | Demo incluye Gateway, pedido, cliente, producto, estado | Parcial justificado |
| FB-07 | Render pendiente | Se crea documentacion de variables, comandos y placeholders | `docs/render-deploy.md` | No se inventan URLs publicas; queda pendiente completar AVA | Pendiente |
| FB-08 | Eureka/discovery | Se agrego `discovery-server` con Eureka, se registraron los 10 microservicios y el Gateway enruta con `lb://` | `discovery-server/`, configuraciones Eureka, `api-gateway/src/main/resources/application.yml` | Commits `b7a3477`, `e56abca`, `bbb5838`, `53d688a`, `0a5377d` | Corregido |
| FB-09 | Documentacion final faltante | Se crea carpeta `docs/` con documentos obligatorios | `docs/*.md`, `docs/pruebas-rest/*.http` | Entregables documentales presentes | Cerrado documental |
| FB-10 | Falta matriz de requerimientos | Se crea matriz con RF/RNF y pruebas asociadas | `docs/matriz-requerimientos.md` | Tabla de requerimientos y evidencias | Cerrado |
| FB-11 | Falta documentacion funcional | Se crea documento funcional con actores, flujos y reglas | `docs/documentacion-funcional.md` | Flujos de pedido, fabricacion, despacho, metricas y logs | Cerrado |
| FB-12 | Falta documentacion tecnica | Se crea documento tecnico con estructura y ejecucion desde cero | `docs/documentacion-tecnica.md` | Secciones obligatorias incluidas | Cerrado |
| FB-13 | Falta evidencia REST | Se crea archivo `.http` con pruebas manuales | `docs/pruebas-rest/casa-de-la-impresion.http` | Requests por Gateway y servicios directos | Cerrado |
| FB-14 | Falta defensa grupal | Se crea guion de presentacion | `docs/presentacion-defensa-grupal/README.md` | Secciones de arquitectura, pruebas, Swagger y feedback | Cerrado documental |
| FB-15 | Falta defensa individual | Se crea documento individual para Sebastian Valderrama | `docs/defensa-individual/valderrama-sebastian.md` | Incluye rol, commits, endpoints, pruebas y checklist | Cerrado documental |
| FB-16 | No exponer credenciales | Se mantiene `.env.example` con placeholders | `.env.example`, README | No hay secretos reales documentados | Cerrado |

## Justificaciones Tecnicas Pendientes

| Tema | Justificacion actual | Riesgo |
|---|---|---|
| Docker Compose parcial | Es una demo minima del flujo principal | Si la pauta exige todos los servicios en Docker, debe ampliarse |
| Render sin URLs reales | Se documenta configuracion, pero faltan servicios publicados | Debe completarse antes de entregar por AVA si es obligatorio |
| Eureka/discovery | `discovery-server` implementa Eureka Server; microservicios y Gateway se registran como clientes; Gateway usa rutas `lb://` | En Docker/Render se debe configurar `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` y levantar discovery primero |
| Migraciones mixtas | H2/JPA permite demo local; Flyway existe solo en algunos servicios | Para produccion estricta, completar migraciones por servicio |

## Evidencia De Correccion Eureka

| Commit | Evidencia |
|---|---|
| `b7a3477 feat: agregar discovery server con eureka` | Agrega modulo `discovery-server` con Eureka Server |
| `e56abca feat: registrar cliente service en eureka` | Registra `cliente-service` como cliente Eureka |
| `bbb5838 feat: registrar servicios base en eureka` | Registra servicios base en Eureka |
| `53d688a feat: registrar servicios restantes en eureka` | Completa registro de microservicios restantes |
| `0a5377d feat: enrutar gateway mediante eureka` | Actualiza Gateway para usar rutas `lb://` |
