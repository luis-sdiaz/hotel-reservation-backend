# Hotel Reservation Backend

Backend para el sistema de reservas de hotel. Proyecto Spring Boot con arquitectura por capas, DTOs, validaciones, manejo de errores y documentación OpenAPI.

## Tecnologias
- Java 21
- Spring Boot 3.2.x
- Spring Web, Spring Data JPA, Spring Validation, Spring Security
- H2 (dev/test)
- Flyway (migraciones)
- OpenAPI / Swagger UI
- JUnit 5, Mockito, MockMvc

## Arquitectura
- **Controller**: recibe requests, valida DTOs y devuelve responses.
- **Service**: logica de negocio y transacciones.
- **Repository**: acceso a datos.
- **DTO / Mapper**: contratos con frontend y mapeos.

## Perfiles
- `dev`: H2 en memoria, H2 Console habilitada.
- `test`: H2 en memoria, sin consola, Flyway deshabilitado.
- `prod`: DB por variables de entorno y Basic Auth.

## Variables de entorno (prod)
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_DIALECT` (opcional, default MySQL)
- `CORS_ALLOWED_ORIGINS`
- `APP_BASIC_USER`
- `APP_BASIC_PASSWORD`

## Endpoints principales
- Clientes
  - `GET /api/clientes`
  - `POST /api/clientes`
  - `GET /api/clientes/{id}`
  - `PUT /api/clientes/{id}`
  - `PATCH /api/clientes/{id}/deactivate`
  - `PATCH /api/clientes/{id}/activate`
- Habitaciones
  - `GET /api/habitaciones`
  - `POST /api/habitaciones`
  - `GET /api/habitaciones/{id}`
  - `PUT /api/habitaciones/{id}`
  - `PATCH /api/habitaciones/{id}/deactivate`
  - `PATCH /api/habitaciones/{id}/activate`
- Reservas
  - `GET /api/reservas`
  - `POST /api/reservas`
  - `GET /api/reservas/{id}`
  - `PATCH /api/reservas/{id}/cancelar`

## OpenAPI / Swagger
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Ejecutar localmente
```powershell
./mvnw.cmd -U spring-boot:run
```

## Ejecutar pruebas
```powershell
./mvnw.cmd -U clean verify
```

## Conectar frontend
- CORS permite: `http://localhost:5173` y `http://localhost:3000` en perfil dev/test.
- En produccion, configurar `CORS_ALLOWED_ORIGINS`.

## Postman
Usa los endpoints de arriba. Los contratos de request/response estan documentados en Swagger.

