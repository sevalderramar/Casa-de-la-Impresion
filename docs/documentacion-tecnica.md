# Documentacion Tecnica

## Arquitectura General

El sistema usa una arquitectura de microservicios Spring Boot con `discovery-server` Eureka real. El `api-gateway` centraliza rutas HTTP, se registra como cliente Eureka y enruta con URIs `lb://` hacia los servicios registrados. Los 10 microservicios de dominio tambien se registran como clientes Eureka y exponen APIs REST documentadas con Swagger/OpenAPI. La comunicacion entre servicios se realiza mediante REST, OpenFeign y discovery Eureka donde corresponde.

El servicio principal del dominio es `pedido-service`, porque concentra la operacion central de crear, consultar y evolucionar pedidos. Los servicios de apoyo entregan clientes, productos, estados, fabricacion, despacho, metricas, transportistas, logs y autenticacion.

# Estructura del repositorio

```text
Casa-de-la-impresion/
  README.md
  .env.example
  docker-compose.yml
  run-all.ps1
  discovery-server/
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
| `discovery-server` | 8761 | Servidor Eureka para registro y descubrimiento de servicios |
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

El Gateway usa Spring Cloud Gateway WebFlux, se registra como cliente Eureka y define rutas centralizadas en `api-gateway/src/main/resources/application.yml`. Las rutas conservan los prefijos `/api/**`, pero el destino se resuelve mediante URIs `lb://` contra el registry de Eureka.

| Ruta | Servicio Eureka | URI Gateway |
|---|---|---|
| `/api/auth/**` | `auth-service` | `lb://auth-service` |
| `/api/pedidos/**` | `pedido-service` | `lb://pedido-service` |
| `/api/clientes/**` | `cliente-service` | `lb://cliente-service` |
| `/api/productos/**` | `producto-service` | `lb://producto-service` |
| `/api/despachos/**` | `despacho-service` | `lb://despacho-service` |
| `/api/fabricacion/**` | `fabricacion-service` | `lb://fabricacion-service` |
| `/api/estados/**` | `estado-service` | `lb://estado-service` |
| `/api/metricas/**` | `metrica-service` | `lb://metrica-service` |
| `/api/transportistas/**` | `transportista-service` | `lb://transportista-service` |
| `/api/logs/**` | `log-service` | `lb://log-service` |

## Discovery

Existe un modulo `discovery-server` que ejecuta Netflix Eureka Server en el puerto local `8761`. La consola queda disponible en `http://localhost:8761` y el endpoint local de registro es `http://localhost:8761/eureka/`.

Los 10 microservicios de dominio se registran como clientes Eureka. El `api-gateway` tambien se registra como cliente Eureka y usa rutas `lb://` hacia los servicios registrados. En Docker y Render, la variable que debe apuntar al servidor Eureka del entorno es `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`.

Despues de iniciar o reiniciar servicios, el Gateway puede responder `503 Service Unavailable` durante algunos segundos mientras refresca el registry de Eureka.

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
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL del servidor Eureka; localmente `http://localhost:8761/eureka/` |
| `PEDIDO_SERVICE_URL` | URL de `pedido-service` para clientes Feign internos donde aplica |
| `CLIENTE_SERVICE_URL` | URL de `cliente-service` para clientes Feign internos donde aplica |
| `PRODUCTO_SERVICE_URL` | URL de `producto-service` para clientes Feign internos donde aplica |
| `ESTADO_SERVICE_URL` | URL de `estado-service` para clientes Feign internos donde aplica |
| `PORT` | Puerto asignado por Render cuando aplica |

## Seguridad JWT

JWT esta implementado mediante filtros y utilidades de seguridad en los servicios. Para facilitar pruebas locales y demo, varios endpoints se mantienen con `permitAll` o configuracion permisiva, especialmente Swagger, H2 y healthchecks. Esto debe explicarse como decision de evaluacion local, no como endurecimiento final de produccion.

## Comunicacion Feign

`pedido-service`, `fabricacion-service`, `despacho-service` y `metrica-service` tienen integraciones o contratos Feign para comunicarse con servicios remotos. Las pruebas cubren casos de errores remotos y tolerancia frente a respuestas Feign no exitosas. El Gateway no depende de URLs HTTP fijas para enrutar: usa Eureka y rutas `lb://`.

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

`docker-compose.yml` corresponde a una demo minima validada del flujo principal con Eureka real. Incluye `discovery-server`, `api-gateway`, `cliente-service`, `producto-service`, `pedido-service` y `estado-service`. No levanta los 10 microservicios de dominio.

La demo Docker usa `discovery-server` en `http://localhost:8761`, `api-gateway` en `http://localhost:8080` y registra en Eureka las aplicaciones `API-GATEWAY`, `CLIENTE-SERVICE`, `PRODUCTO-SERVICE`, `PEDIDO-SERVICE` y `ESTADO-SERVICE`.

Variables relevantes para la demo Docker:

| Variable | Uso |
|---|---|
| `JWT_SECRET` | Secreto temporal/local para servicios demo |
| `JWT_EXPIRATION_MS` | Duracion del token JWT |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://discovery-server:8761/eureka/` |

Comandos validados:

```powershell
$env:JWT_SECRET="clave-temporal-local-para-validacion-final-123456789"
$env:JWT_EXPIRATION_MS="86400000"
docker compose config
docker compose build
docker compose up -d
docker compose ps
curl.exe http://localhost:8761
curl.exe http://localhost:8080/actuator/health
curl.exe http://localhost:8080/api/clientes
curl.exe http://localhost:8080/api/productos
curl.exe http://localhost:8080/api/pedidos
docker compose down
```

El Gateway puede tardar algunos segundos en resolver rutas `lb://` mientras refresca el registry de Eureka. Si aparece `503 Service Unavailable` inmediatamente despues del arranque, esperar 20-40 segundos y reintentar.

## Render

Render esta parcialmente avanzado: `discovery-server` ya fue desplegado y validado en `https://discovery-server-gjd0.onrender.com`, con endpoint Eureka `https://discovery-server-gjd0.onrender.com/eureka/`. El `api-gateway` y los microservicios de dominio siguen pendientes de URLs reales; la documentacion de despliegue mantiene placeholders para esos servicios en `docs/render-deploy.md`.

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
cd ..\discovery-server; .\mvnw.cmd clean verify
```

## Ejecutar Servicios Localmente

Levantar cada servicio en una terminal distinta con su Maven Wrapper. El orden recomendado desde cero es:

1. `discovery-server`
2. microservicios de dominio
3. `api-gateway`

Primero iniciar Eureka:

```powershell
cd discovery-server
.\mvnw.cmd spring-boot:run
```

Verificar la consola en `http://localhost:8761`.

Luego iniciar los microservicios:

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
