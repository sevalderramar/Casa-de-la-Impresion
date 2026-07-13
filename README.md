# Casa de la Impresion - Microservicios

Sistema backend para la gestion de pedidos de Casa de la Impresion. El proyecto esta organizado como una arquitectura de microservicios Spring Boot con API Gateway, persistencia H2 para desarrollo local, seguridad JWT, comunicacion REST/OpenFeign, documentacion Swagger/OpenAPI en los 10 microservicios de dominio y una demo Docker minima para los servicios principales del flujo de pedidos.

## Arquitectura

El sistema se compone de 10 microservicios de dominio mas un `api-gateway`. Cada microservicio mantiene su propia responsabilidad y, cuando corresponde, se comunica con otros servicios mediante HTTP/OpenFeign.

| Servicio | Puerto | Responsabilidad |
|---|---:|---|
| api-gateway | 8080 | Entrada central y enrutamiento hacia microservicios |
| pedido-service | 8081 | Gestion de pedidos e integracion con cliente, producto y estado |
| cliente-service | 8082 | Gestion de clientes |
| producto-service | 8083 | Gestion de productos |
| despacho-service | 8084 | Gestion de despachos |
| fabricacion-service | 8085 | Gestion de ordenes de fabricacion |
| estado-service | 8086 | Registro y consulta de cambios de estado |
| metrica-service | 8087 | Metricas del sistema |
| transportista-service | 8088 | Gestion de transportistas |
| log-service | 8089 | Registro de logs |
| auth-service | 8090 | Autenticacion JWT y usuarios |

## Estado Final Para Defensa

Auditoria final del proyecto completo:

| Evidencia | Resultado |
|---|---:|
| Tests automatizados pasando | 452 |
| Microservicios con JaCoCo configurado | 10 |
| Microservicios sobre 80% de cobertura de lineas | 10 |
| Microservicios con Swagger/OpenAPI | 10 |
| API Gateway compilando | Si |

Nota sobre `api-gateway`: el gateway compila correctamente y enruta hacia los microservicios, pero no se cuenta dentro de los 10 microservicios de dominio para JaCoCo, cobertura ni Swagger. Su responsabilidad es de infraestructura/enrutamiento y no expone controladores de negocio propios.

## API Gateway

El Gateway corre en `http://localhost:8080` y enruta hacia los servicios internos. Las rutas principales configuradas para la demo y el uso local son:

| Ruta Gateway | Servicio destino local |
|---|---|
| `/api/clientes/**` | `cliente-service` en `8082` |
| `/api/productos/**` | `producto-service` en `8083` |
| `/api/pedidos/**` | `pedido-service` en `8081` |
| `/api/estados/**` | `estado-service` en `8086` |

Las URLs de destino del Gateway usan variables de entorno con fallback local:

```properties
CLIENTE_SERVICE_URL=http://localhost:8082
PRODUCTO_SERVICE_URL=http://localhost:8083
PEDIDO_SERVICE_URL=http://localhost:8081
ESTADO_SERVICE_URL=http://localhost:8086
```

## Seguridad

Los servicios usan JWT mediante una variable de entorno compartida:

```powershell
$env:JWT_SECRET="<TU_JWT_SECRET_BASE64>"
$env:JWT_EXPIRATION_MS="86400000"
```

No se deben versionar claves reales. El archivo `.env.example` solo contiene nombres de variables y placeholders.

## Ejecucion Local Con Maven

Configura primero `JWT_SECRET` en la terminal donde levantaras los servicios:

```powershell
$env:JWT_SECRET="<TU_JWT_SECRET_BASE64>"
$env:JWT_EXPIRATION_MS="86400000"
```

Ejemplo para ejecutar un servicio:

```powershell
cd cliente-service
.\mvnw spring-boot:run
```

Para el flujo principal por Gateway, levantar en terminales separadas:

```powershell
cd estado-service
.\mvnw spring-boot:run
```

```powershell
cd cliente-service
.\mvnw spring-boot:run
```

```powershell
cd producto-service
.\mvnw spring-boot:run
```

```powershell
cd pedido-service
$env:CLIENTE_SERVICE_URL="http://localhost:8082"
$env:PRODUCTO_SERVICE_URL="http://localhost:8083"
$env:ESTADO_SERVICE_URL="http://localhost:8086"
.\mvnw spring-boot:run
```

```powershell
cd api-gateway
$env:CLIENTE_SERVICE_URL="http://localhost:8082"
$env:PRODUCTO_SERVICE_URL="http://localhost:8083"
$env:PEDIDO_SERVICE_URL="http://localhost:8081"
$env:ESTADO_SERVICE_URL="http://localhost:8086"
.\mvnw spring-boot:run
```

## Demo Docker

La demo Docker actual incluye solo los servicios necesarios para probar el flujo principal por Gateway:

- `api-gateway`
- `cliente-service`
- `producto-service`
- `pedido-service`
- `estado-service`

Servicios fuera de esta demo Docker inicial:

- `auth-service`
- `despacho-service`
- `fabricacion-service`
- `metrica-service`
- `transportista-service`
- `log-service`

Antes de levantar contenedores, configura `JWT_SECRET` en el entorno o en un archivo `.env` local no versionado:

```powershell
$env:JWT_SECRET="<TU_JWT_SECRET_BASE64>"
$env:JWT_EXPIRATION_MS="86400000"
```

Comandos Docker:

```powershell
docker compose build
```

```powershell
docker compose up -d
```

```powershell
docker compose ps
```

```powershell
docker compose down
```

El `docker-compose.yml` configura `SPRING_PROFILES_ACTIVE=h2` para los servicios de la demo. `pedido-service` se conecta por nombres internos de Docker:

```properties
CLIENTE_SERVICE_URL=http://cliente-service:8082
PRODUCTO_SERVICE_URL=http://producto-service:8083
ESTADO_SERVICE_URL=http://estado-service:8086
```

El Gateway tambien recibe esas URLs internas para enrutar dentro de la red Docker.

## Comandos De Prueba

Con los servicios levantados, probar por Gateway:

```powershell
curl.exe http://localhost:8080/actuator/health
```

```powershell
curl.exe http://localhost:8080/api/clientes
```

```powershell
curl.exe http://localhost:8080/api/productos
```

```powershell
curl.exe http://localhost:8080/api/pedidos
```

## Persistencia H2

Los servicios usan perfil `h2` para desarrollo local y demo Docker. Cada servicio mantiene su base H2 propia bajo su directorio de ejecucion.

Consolas H2 locales principales:

| Servicio | URL |
|---|---|
| pedido-service | `http://localhost:8081/h2-console` |
| cliente-service | `http://localhost:8082/h2-console` |
| producto-service | `http://localhost:8083/h2-console` |
| estado-service | `http://localhost:8086/h2-console` |

Credenciales H2 de desarrollo:

- Usuario: `sa`
- Password: vacio

## Swagger/OpenAPI

Swagger/OpenAPI esta configurado en los 10 microservicios de dominio mediante Springdoc y `OpenApiConfig`. Cada controller principal tiene `@Tag` y sus endpoints tienen `@Operation`.

URLs esperadas con los servicios levantados localmente:

| Servicio | Swagger UI | API Docs |
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

## Tests

La suite final tiene 452 tests pasando. Los 10 microservicios de dominio tienen JaCoCo configurado y superan 80% de cobertura de lineas.

Resumen por modulo:

| Servicio | Tests | Cobertura lineas | JaCoCo |
|---|---:|---:|---|
| auth-service | 50 | 86.38% | Si |
| cliente-service | 36 | 83.33% | Si |
| producto-service | 42 | 86.39% | Si |
| pedido-service | 44 | 83.67% | Si |
| estado-service | 33 | 91.97% | Si |
| despacho-service | 48 | 95.88% | Si |
| fabricacion-service | 76 | 96.76% | Si |
| metrica-service | 46 | 92.82% | Si |
| transportista-service | 40 | 90.65% | Si |
| log-service | 37 | 91.22% | Si |
| api-gateway | 0 | N/A | No |

Comandos para generar evidencias de build, tests y cobertura:

| Evidencia | Comando |
|---|---|
| Estado Git | `git status` |
| Historial reciente | `git log --oneline -12` |
| Build auth-service | `cd auth-service; .\mvnw.cmd clean verify` |
| Build cliente-service | `cd cliente-service; .\mvnw.cmd clean verify` |
| Build producto-service | `cd producto-service; .\mvnw.cmd clean verify` |
| Build pedido-service | `cd pedido-service; .\mvnw.cmd clean verify` |
| Build estado-service | `cd estado-service; .\mvnw.cmd clean verify` |
| Build despacho-service | `cd despacho-service; .\mvnw.cmd clean verify` |
| Build fabricacion-service | `cd fabricacion-service; .\mvnw.cmd clean verify` |
| Build metrica-service | `cd metrica-service; .\mvnw.cmd clean verify` |
| Build transportista-service | `cd transportista-service; .\mvnw.cmd clean verify` |
| Build log-service | `cd log-service; .\mvnw.cmd clean verify` |
| Build api-gateway | `cd api-gateway; .\mvnw.cmd clean verify` |
| Reporte JaCoCo auth-service | `Start-Process auth-service\target\site\jacoco\index.html` |
| Reporte JaCoCo cliente-service | `Start-Process cliente-service\target\site\jacoco\index.html` |
| Reporte JaCoCo producto-service | `Start-Process producto-service\target\site\jacoco\index.html` |
| Reporte JaCoCo pedido-service | `Start-Process pedido-service\target\site\jacoco\index.html` |
| Reporte JaCoCo estado-service | `Start-Process estado-service\target\site\jacoco\index.html` |
| Reporte JaCoCo despacho-service | `Start-Process despacho-service\target\site\jacoco\index.html` |
| Reporte JaCoCo fabricacion-service | `Start-Process fabricacion-service\target\site\jacoco\index.html` |
| Reporte JaCoCo metrica-service | `Start-Process metrica-service\target\site\jacoco\index.html` |
| Reporte JaCoCo transportista-service | `Start-Process transportista-service\target\site\jacoco\index.html` |
| Reporte JaCoCo log-service | `Start-Process log-service\target\site\jacoco\index.html` |

## Tecnologias

- Java 21
- Spring Boot 4.0.5
- Maven
- Spring Data JPA
- Spring Security
- JWT
- OpenFeign
- H2 Database
- Swagger/OpenAPI con Springdoc en los 10 microservicios de dominio
- Docker Compose para demo minima
- JUnit 5 y Mockito en servicios con pruebas unitarias
