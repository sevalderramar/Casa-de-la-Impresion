# Log Service

> Microservicio de registro centralizado de eventos operativos del ecosistema **Api Pedidos**.

## Descripcion General

`log-service` recibe y persiste eventos de auditoria y trazabilidad generados por los demas microservicios del sistema. Funciona como un receptor pasivo con persistencia local en H2 y expone consultas simples por servicio y fecha.

### Responsabilidades Principales

| Responsabilidad | Descripcion |
|---|---|
| **Registro de logs** | Persistir eventos operativos enviados por otros servicios |
| **Consulta de logs** | Filtrar logs por servicio y/o fecha desde |
| **Healthcheck** | Exponer un endpoint de verificacion del servicio |
| **Persistencia local** | Guardar datos en H2 file-based |

## Stack Tecnologico

| Tecnologia | Version / Detalle |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.5 |
| Spring Data JPA | Hibernate como ORM |
| Lombok | `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@Builder` |
| Bean Validation | `jakarta.validation` |
| H2 Database | Base embebida en archivo |

## Puerto por Defecto (documentado)

```
http://localhost:8089
```

## Inicio Rapido

```powershell
.\mvnw.cmd -f log-service\pom.xml spring-boot:run
```

## Estructura del Modulo

```text
log-service/
├── pom.xml
├── docs/
│   ├── README.md
│   ├── API.md
│   ├── ARQUITECTURA.md
│   ├── CONFIGURACION.md
│   └── MODELO_DATOS.md
└── src/main/
    ├── java/cl/duocuc/logservice/
    │   ├── LogServiceApplication.java
    │   ├── config/
    │   │   └── GlobalExceptionHandler.java
    │   ├── controller/
    │   │   └── LogController.java
    │   ├── dto/
    │   │   ├── ApiResponse.java
    │   │   └── LogRequestDTO.java
    │   ├── entity/
    │   │   └── LogEntrada.java
    │   ├── repository/
    │   │   └── LogRepository.java
    │   └── service/
    │       ├── LogService.java
    │       └── LogServiceImpl.java
    └── resources/
        ├── application.properties
        └── application-h2.properties
```

## Documentacion Detallada

| Documento | Descripcion |
|---|---|
| [API.md](API.md) | Endpoints REST y formatos de request/response |
| [ARQUITECTURA.md](ARQUITECTURA.md) | Capas, flujo de datos y responsabilidades |
| [MODELO_DATOS.md](MODELO_DATOS.md) | Entidad persistida y columnas |
| [CONFIGURACION.md](CONFIGURACION.md) | Perfiles, propiedades y consola H2 |

## Posicion en el Ecosistema

```text
cliente-service ──POST /api/logs──►
pedido-service  ──POST /api/logs──►  log-service (8089) ──► H2 (db_logs)
fabricacion-service ──POST /api/logs──►
```

`log-service` no consume otros microservicios. Su rol es centralizar trazabilidad y auditoria.

---

*Proyecto academico — Arquitectura Fullstack, DuocUC.*
