# Despliegue Render

## Estado Actual

La configuracion de Render queda documentada y preparada a nivel de instrucciones. No hay evidencia en el repositorio de URLs publicas reales desplegadas, por lo tanto no se afirma que Render ya este operativo.

Las URLs son placeholders y deben reemplazarse por las URLs reales generadas por Render antes de registrar la entrega en AVA.

## URLs Placeholder

| Servicio | URL placeholder |
|---|---|
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
| `api-gateway` | `PEDIDO_SERVICE_URL`, `CLIENTE_SERVICE_URL`, `PRODUCTO_SERVICE_URL`, `ESTADO_SERVICE_URL` y opcionalmente URLs de los demas servicios si se externalizan todas las rutas |
| `pedido-service` | `CLIENTE_SERVICE_URL`, `PRODUCTO_SERVICE_URL`, `ESTADO_SERVICE_URL`, `JWT_SECRET`, `JWT_EXPIRATION_MS` |
| `metrica-service` | URLs de servicios consultados para metricas si aplica, `JWT_SECRET`, `JWT_EXPIRATION_MS` |
| `fabricacion-service` | `PEDIDO_SERVICE_URL` si usa integracion remota, `JWT_SECRET`, `JWT_EXPIRATION_MS` |
| `despacho-service` | `PEDIDO_SERVICE_URL` si usa integracion remota, `JWT_SECRET`, `JWT_EXPIRATION_MS` |
| Resto de microservicios | `JWT_SECRET`, `JWT_EXPIRATION_MS`, variables de base de datos si se reemplaza H2 |

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

1. `auth-service`
2. `cliente-service`
3. `producto-service`
4. `estado-service`
5. `pedido-service`
6. `despacho-service`
7. `fabricacion-service`
8. `transportista-service`
9. `log-service`
10. `metrica-service`
11. `api-gateway`

El Gateway debe configurarse al final porque necesita conocer las URLs publicas reales de los servicios destino.

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

El proyecto utiliza discovery estatico por configuracion del Gateway mediante rutas y variables de entorno. No se implemento Eureka Server real. El Gateway debe recibir las URLs publicas de cada servicio cuando sean publicadas en Render.

Ejemplo:

```properties
PEDIDO_SERVICE_URL=https://<pedido-service>.onrender.com
CLIENTE_SERVICE_URL=https://<cliente-service>.onrender.com
PRODUCTO_SERVICE_URL=https://<producto-service>.onrender.com
ESTADO_SERVICE_URL=https://<estado-service>.onrender.com
```

## Base De Datos

Para defensa local se usa H2. Para Render productivo, evaluar una base externa persistente por servicio o mantener H2 solo como demo temporal. Si se usa base persistente, revisar `application-prod.properties`, migraciones Flyway y `ddl-auto=validate`.

## Checklist Antes De AVA

| Item | Estado |
|---|---|
| URLs reales Render reemplazadas | Pendiente |
| Variables `JWT_SECRET` configuradas en Render | Pendiente |
| Gateway apuntando a URLs reales | Pendiente |
| Swagger probado en servicios desplegados | Pendiente |
| `/actuator/health` probado donde aplique | Pendiente |
| Base persistente definida o H2 justificado | Pendiente |
