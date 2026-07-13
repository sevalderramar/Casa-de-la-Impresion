# Despliegue Render

## Estado Actual

La configuracion de Render queda documentada y preparada a nivel de instrucciones. No hay evidencia en el repositorio de URLs publicas reales desplegadas, por lo tanto no se afirma que Render ya este operativo.

Las URLs son placeholders y deben reemplazarse por las URLs reales generadas por Render antes de registrar la entrega en AVA.

## URLs Placeholder

| Servicio | URL placeholder |
|---|---|
| `discovery-server` | `https://<discovery-server>.onrender.com` |
| `api-gateway` | `https://<api-gateway>.onrender.com` |
| `auth-service` | `https://<auth-service>.onrender.com` |
| `pedido-service` | `https://<pedido-service>.onrender.com` |
| `cliente-service` | `https://<cliente-service>.onrender.com` |
| `producto-service` | `https://<producto-service>.onrender.com` |
| `estado-service` | `https://<estado-service>.onrender.com` |
| `despacho-service` | `https://<despacho-service>.onrender.com` |
| `fabricacion-service` | `https://<fabricacion-service>.onrender.com` |
| `metrica-service` | `https://<metrica-service>.onrender.com` |
| `transportista-service` | `https://<transportista-service>.onrender.com` |
| `log-service` | `https://<log-service>.onrender.com` |

Usar estos valores solo como placeholders. Reemplazar por las URLs reales asignadas por Render antes de la entrega formal.

## Variables De Entorno Globales

| Variable | Valor sugerido |
|---|---|
| `JWT_SECRET` | Secreto Base64 real, no versionado |
| `JWT_EXPIRATION_MS` | `86400000` |
| `SPRING_PROFILES_ACTIVE` | `prod` o `h2` segun estrategia de despliegue |
| `PORT` | Render lo asigna automaticamente |

## Variables Por Servicio

| Servicio | Variables adicionales |
|---|---|
| `discovery-server` | Variables globales si aplica |
| `api-gateway` | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://<discovery-server>.onrender.com/eureka/` |
| `pedido-service` | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`, `CLIENTE_SERVICE_URL`, `PRODUCTO_SERVICE_URL`, `ESTADO_SERVICE_URL`, `JWT_SECRET`, `JWT_EXPIRATION_MS` |
| `metrica-service` | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`, URLs de servicios consultados para metricas si aplica, `JWT_SECRET`, `JWT_EXPIRATION_MS` |
| `fabricacion-service` | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`, `PEDIDO_SERVICE_URL` si usa integracion remota, `JWT_SECRET`, `JWT_EXPIRATION_MS` |
| `despacho-service` | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`, `PEDIDO_SERVICE_URL` si usa integracion remota, `JWT_SECRET`, `JWT_EXPIRATION_MS` |
| Resto de microservicios | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, variables de base de datos si se reemplaza H2 |

## Comando Build

En Render, configurar cada servicio desde su subdirectorio correspondiente.

```bash
./mvnw clean package -DskipTests
```

En Windows local se usa `.\mvnw.cmd`, pero Render ejecuta Linux y debe usar `./mvnw`.

## Comando Start

```bash
java -jar target/*.jar
```

Si Render requiere un jar exacto, usar el nombre generado por cada modulo, por ejemplo:

```bash
java -jar target/pedido-service-0.0.1-SNAPSHOT.jar
```

## Puertos

| Servicio | Puerto local | Render |
|---|---:|---|
| `discovery-server` | 8761 | Publicar servicio y usar su URL en `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` |
| `api-gateway` | 8080 | Usa `PORT` |
| `pedido-service` | 8081 | Usa `PORT` |
| `cliente-service` | 8082 | Usa `PORT` |
| `producto-service` | 8083 | Usa `PORT` |
| `despacho-service` | 8084 | Usa `PORT` |
| `fabricacion-service` | 8085 | Usa `PORT` |
| `estado-service` | 8086 | Usa `PORT` |
| `metrica-service` | 8087 | Usa `PORT` |
| `transportista-service` | 8088 | Usa `PORT` |
| `log-service` | 8089 | Usa `PORT` |
| `auth-service` | 8090 | Usa `PORT` |

Los servicios ya tienen `application-prod.properties` con `server.port=${PORT:puerto-local-del-servicio}` en los microservicios. Revisar `api-gateway` antes de Render si se necesita soporte explicito de `PORT`.

## Orden Sugerido De Despliegue

1. `discovery-server`
2. `auth-service`
3. `cliente-service`
4. `producto-service`
5. `estado-service`
6. `pedido-service`
7. `despacho-service`
8. `fabricacion-service`
9. `transportista-service`
10. `log-service`
11. `metrica-service`
12. `api-gateway`

`discovery-server` debe publicarse primero para que los microservicios y el Gateway puedan registrarse. El Gateway debe configurarse al final porque depende del registry de Eureka y enruta mediante `lb://` hacia los servicios registrados.

## Como Configurar Cada Servicio En Render

1. Crear nuevo Web Service desde el repositorio.
2. Seleccionar el subdirectorio del servicio como root directory.
3. Usar entorno Java.
4. Configurar build command: `./mvnw clean package -DskipTests`.
5. Configurar start command: `java -jar target/*.jar`.
6. Agregar variables de entorno requeridas.
7. Verificar `/actuator/health` si el servicio lo expone.
8. Verificar Swagger en `/swagger-ui/index.html`.
9. Copiar URL publica real al documento final antes de AVA.

## Discovery En Render

El proyecto utiliza `discovery-server` con Eureka Server. En Render se debe crear un Web Service para `discovery-server` y usar su URL publica como `defaultZone` de todos los clientes Eureka.

Ejemplo:

```properties
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://<discovery-server>.onrender.com/eureka/
```

El `api-gateway` se registra como cliente Eureka y usa rutas `lb://`, por lo que no se deben documentar URLs reales de servicios mientras no existan despliegues verificados.

## Referencia Docker Validada

La validacion local con Docker Compose ya funciona como demo minima del flujo principal con Eureka. Esta validacion no implica que Render este desplegado.

Servicios incluidos en la demo Docker validada:

- `discovery-server`
- `api-gateway`
- `cliente-service`
- `producto-service`
- `pedido-service`
- `estado-service`

Variables relevantes usadas localmente:

```properties
JWT_SECRET=clave-temporal-local-para-validacion-final-123456789
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-server:8761/eureka/
```

En Render se debe reemplazar el `defaultZone` por `https://<discovery-server>.onrender.com/eureka/` cuando exista URL real.

## Base De Datos

Para defensa local se usa H2. Para Render productivo, evaluar una base externa persistente por servicio o mantener H2 solo como demo temporal. Si se usa base persistente, revisar `application-prod.properties`, migraciones Flyway y `ddl-auto=validate`.

## Checklist Antes De AVA

| Item | Estado |
|---|---|
| URLs reales Render reemplazadas | Pendiente |
| Variables `JWT_SECRET` configuradas en Render | Pendiente |
| `discovery-server` desplegado primero | Pendiente |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` configurado en servicios y Gateway | Pendiente |
| Gateway registrado en Eureka y rutas `lb://` verificadas | Pendiente |
| Swagger probado en servicios desplegados | Pendiente |
| `/actuator/health` probado donde aplique | Pendiente |
| Base persistente definida o H2 justificado | Pendiente |
