# Documentacion Tecnica

## Arquitectura General

El sistema usa una arquitectura de microservicios Spring Boot. El `api-gateway` centraliza rutas HTTP y los 10 microservicios de dominio exponen APIs REST documentadas con Swagger/OpenAPI. La comunicacion entre servicios se realiza mediante REST y OpenFeign donde corresponde.

El servicio principal del dominio es `pedido-service`, porque concentra la operacion central de crear, consultar y evolucionar pedidos. Los servicios de apoyo entregan clientes, productos, estados, fabricacion, despacho, metricas, transportistas, logs y autenticacion.

# Estructura del repositorio

```text
Casa-de-la-impresion/
  README.md
  .env.example
  docker-compose.yml
  run-all.ps1
  api-gateway/
  auth-service/
  cliente-service/
  producto-service/
  pedido-service/
  estado-service/
  despacho-service/
  fabricacion-service/
  metrica-service/
  transportista-service/
  log-service/
  docs/
```

## Responsabilidades Por Servicio

| Servicio | Puerto | Responsabilidad |
|---|---:|---|
| `api-gateway` | 8080 | Entrada central y rutas hacia microservicios |
| `pedido-service` | 8081 | Gestion principal de pedidos e integraciones |
| `cliente-service` | 8082 | Gestion de clientes |
| `producto-service` | 8083 | Gestion de productos/catalogo |
| `despacho-service` | 8084 | Gestion de despachos |
| `fabricacion-service` | 8085 | Gestion de ordenes de fabricacion |
| `estado-service` | 8086 | Registro y consulta de estados de pedidos |
| `metrica-service` | 8087 | Metricas de clientes, productos y ventas |
| `transportista-service` | 8088 | Gestion de transportistas |
| `log-service` | 8089 | Registro y consulta de logs del sistema |
| `auth-service` | 8090 | Autenticacion JWT y usuarios |

## Gateway

El Gateway usa Spring Cloud Gateway WebFlux y define rutas centralizadas en `api-gateway/src/main/resources/application.yml`.

| Ruta | Servicio |
|---|---|
| `/api/auth/**` | `auth-service` |
| `/api/pedidos/**` | `pedido-service` |
| `/api/clientes/**` | `cliente-service` |
| `/api/productos/**` | `producto-service` |
| `/api/despachos/**` | `despacho-service` |
| `/api/fabricacion/**` | `fabricacion-service` |
| `/api/estados/**` | `estado-service` |
| `/api/metricas/**` | `metrica-service` |
| `/api/transportistas/**` | `transportista-service` |
| `/api/logs/**` | `log-service` |

## Discovery

No existe Eureka Server real en el repositorio. El discovery actual se resuelve mediante configuracion estatica y variables de entorno en Gateway, por ejemplo `PEDIDO_SERVICE_URL`, `CLIENTE_SERVICE_URL`, `PRODUCTO_SERVICE_URL` y `ESTADO_SERVICE_URL`. Esta decision simplifica la demo local y Docker Compose, pero debe declararse en la defensa si la pauta pregunta por service discovery.

## Modelo De Datos Resumido

| Servicio | Entidades o datos principales |
|---|---|
| `auth-service` | Usuarios, credenciales, roles, JWT |
| `cliente-service` | Cliente, RUT, contacto, direccion |
| `producto-service` | Producto, nombre, categoria, precio, stock |
| `pedido-service` | Pedido, detalle de pedido, cliente, productos, numero de pedido |
| `estado-service` | Cambio de estado, numero de pedido, estado, timestamp |
| `despacho-service` | Despacho, numero de pedido, tipo, transportista, seguimiento |
| `fabricacion-service` | Orden de fabricacion, historial, estado productivo |
| `metrica-service` | Resumenes calculados, ranking, ventas, top productos |
| `transportista-service` | Transportista, codigo, contacto, regiones, activo |
| `log-service` | LogEntrada, servicio, operacion, usuario, resultado, detalle |

## Perfiles Local, H2 Y Prod

Cada microservicio mantiene configuracion propia con `application.properties`, `application-h2.properties` y `application-prod.properties` cuando aplica. El perfil `h2` permite ejecucion local con base embebida. En `prod`, varios servicios usan `ddl-auto=validate`, por lo que se debe revisar migracion o esquema antes de desplegar en base persistente.

## Persistencia Y Migraciones

Hay migraciones Flyway reales en algunos servicios:

| Servicio | Evidencia |
|---|---|
| `cliente-service` | `src/main/resources/db/migration/V1__create_clientes_table.sql` |
| `despacho-service` | `src/main/resources/db/migration/V1__create_despachos_table.sql` |
| `fabricacion-service` | `src/main/resources/db/migration/V1__fabricacion_initial_schema.sql` |
| `transportista-service` | `src/main/resources/data.sql` |

Para los demas servicios, la justificacion actual es uso de JPA/Hibernate con H2 en modo local/demo. Si se exige produccion real, conviene completar migraciones por servicio antes de Render con base persistente.

## Variables De Entorno

| Variable | Uso |
|---|---|
| `JWT_SECRET` | Secreto base para firmar y validar tokens JWT |
| `JWT_EXPIRATION_MS` | Duracion del token JWT |
| `PEDIDO_SERVICE_URL` | URL de `pedido-service` para Gateway o clientes Feign |
| `CLIENTE_SERVICE_URL` | URL de `cliente-service` |
| `PRODUCTO_SERVICE_URL` | URL de `producto-service` |
| `ESTADO_SERVICE_URL` | URL de `estado-service` |
| `PORT` | Puerto asignado por Render cuando aplica |

## Seguridad JWT

JWT esta implementado mediante filtros y utilidades de seguridad en los servicios. Para facilitar pruebas locales y demo, varios endpoints se mantienen con `permitAll` o configuracion permisiva, especialmente Swagger, H2 y healthchecks. Esto debe explicarse como decision de evaluacion local, no como endurecimiento final de produccion.

## Comunicacion Feign

`pedido-service`, `fabricacion-service`, `despacho-service` y `metrica-service` tienen integraciones o contratos Feign para comunicarse con servicios remotos. Las pruebas cubren casos de errores remotos y tolerancia frente a respuestas Feign no exitosas.

## Manejo De Errores

Los servicios incluyen handlers o excepciones globales para validar entradas, manejar errores de negocio y traducir errores remotos. La cobertura de tests incluye handlers en varios servicios y casos invalidos en controllers.

## Logs

`log-service` registra eventos operacionales con servicio, operacion, usuario, resultado, detalle y timestamp. Tambien permite consultar logs con filtros opcionales por servicio y fecha.

## Pruebas

La suite final tiene 452 tests pasando. Hay pruebas unitarias y de controller para los 10 microservicios de dominio. El `api-gateway` compila, pero no tiene tests porque su responsabilidad actual es declarativa/enrutamiento.

## JaCoCo

Los 10 microservicios de dominio tienen JaCoCo configurado y superan 80% de cobertura de lineas.

| Servicio | Tests | Cobertura lineas |
|---|---:|---:|
| `auth-service` | 50 | 86.38% |
| `cliente-service` | 36 | 83.33% |
| `producto-service` | 42 | 86.39% |
| `pedido-service` | 44 | 83.67% |
| `estado-service` | 33 | 91.97% |
| `despacho-service` | 48 | 95.88% |
| `fabricacion-service` | 76 | 96.76% |
| `metrica-service` | 46 | 92.82% |
| `transportista-service` | 40 | 90.65% |
| `log-service` | 37 | 91.22% |

## Swagger

Los 10 microservicios de dominio tienen Swagger/OpenAPI con Springdoc.

| Servicio | Swagger UI | OpenAPI JSON |
|---|---|---|
| auth-service | `http://localhost:8090/swagger-ui/index.html` | `http://localhost:8090/v3/api-docs` |
| cliente-service | `http://localhost:8082/swagger-ui/index.html` | `http://localhost:8082/v3/api-docs` |
| producto-service | `http://localhost:8083/swagger-ui/index.html` | `http://localhost:8083/v3/api-docs` |
| pedido-service | `http://localhost:8081/swagger-ui/index.html` | `http://localhost:8081/v3/api-docs` |
| estado-service | `http://localhost:8086/swagger-ui/index.html` | `http://localhost:8086/v3/api-docs` |
| despacho-service | `http://localhost:8084/swagger-ui/index.html` | `http://localhost:8084/v3/api-docs` |
| fabricacion-service | `http://localhost:8085/swagger-ui/index.html` | `http://localhost:8085/v3/api-docs` |
| metrica-service | `http://localhost:8087/swagger-ui/index.html` | `http://localhost:8087/v3/api-docs` |
| transportista-service | `http://localhost:8088/swagger-ui/index.html` | `http://localhost:8088/v3/api-docs` |
| log-service | `http://localhost:8089/swagger-ui/index.html` | `http://localhost:8089/v3/api-docs` |

## Docker Compose

`docker-compose.yml` esta preparado como demo minima del flujo principal: `api-gateway`, `pedido-service`, `cliente-service`, `producto-service` y `estado-service`. No incluye todos los microservicios, lo que debe declararse como alcance de demo.

## Render

Render esta pendiente/configurable. No hay URLs publicas reales versionadas ni `render.yaml` existente. La documentacion de despliegue esta en `docs/render-deploy.md` con placeholders y variables necesarias.

# Ejecución desde cero

## Requisitos

| Herramienta | Version recomendada |
|---|---|
| Java | 21 |
| Maven Wrapper | Incluido por servicio |
| Docker | Opcional para demo minima |
| PowerShell | Para comandos Windows del proyecto |

## Variables Iniciales

```powershell
$env:JWT_SECRET="<TU_JWT_SECRET_BASE64>"
$env:JWT_EXPIRATION_MS="86400000"
```

## Compilar Y Probar Todos Los Modulos

```powershell
cd auth-service; .\mvnw.cmd clean verify
cd ..\cliente-service; .\mvnw.cmd clean verify
cd ..\producto-service; .\mvnw.cmd clean verify
cd ..\pedido-service; .\mvnw.cmd clean verify
cd ..\estado-service; .\mvnw.cmd clean verify
cd ..\despacho-service; .\mvnw.cmd clean verify
cd ..\fabricacion-service; .\mvnw.cmd clean verify
cd ..\metrica-service; .\mvnw.cmd clean verify
cd ..\transportista-service; .\mvnw.cmd clean verify
cd ..\log-service; .\mvnw.cmd clean verify
cd ..\api-gateway; .\mvnw.cmd clean verify
```

## Ejecutar Servicios Localmente

Levantar cada servicio en una terminal distinta con su Maven Wrapper:

```powershell
cd cliente-service
.\mvnw.cmd spring-boot:run
```

Para `pedido-service`, configurar URLs remotas si se requiere:

```powershell
$env:CLIENTE_SERVICE_URL="http://localhost:8082"
$env:PRODUCTO_SERVICE_URL="http://localhost:8083"
$env:ESTADO_SERVICE_URL="http://localhost:8086"
.\mvnw.cmd spring-boot:run
```

Para el Gateway:

```powershell
cd api-gateway
$env:PEDIDO_SERVICE_URL="http://localhost:8081"
$env:CLIENTE_SERVICE_URL="http://localhost:8082"
$env:PRODUCTO_SERVICE_URL="http://localhost:8083"
$env:ESTADO_SERVICE_URL="http://localhost:8086"
.\mvnw.cmd spring-boot:run
```

## Ejecutar Demo Docker

```powershell
docker compose build
docker compose up -d
docker compose ps
```

## Ver Evidencias

```powershell
Start-Process pedido-service\target\site\jacoco\index.html
Start-Process log-service\target\site\jacoco\index.html
```
