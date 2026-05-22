# Configuración - auth-service

## Archivos de propiedades

| Archivo | Proposito |
|---|---|
| `application.properties` | Configuración base compartida |
| `application-h2.properties` | Desarrollo local con H2 |
| `application-prod.properties` | Producción con variables de entorno |

## Base (`application.properties`)
```properties
spring.application.name=auth-service
spring.profiles.active=h2
jwt.secret=${JWT_SECRET}
jwt.expiration-ms=${JWT_EXPIRATION_MS:86400000}
```

## Local (`application-h2.properties`)
```properties
server.port=8090
spring.datasource.url=jdbc:h2:mem:auth_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.flyway.enabled=false
```

## Producción (`application-prod.properties`)
```properties
server.port=${PORT:8090}
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.h2.console.enabled=false
spring.flyway.enabled=false
```

## Variables de entorno
- `JWT_SECRET` (obligatoria)
- `JWT_EXPIRATION_MS` (opcional)
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (prod)
- `PORT` (opcional en prod)

## Notas
- No se documentan claves reales.
- H2 console solo para `h2`.
