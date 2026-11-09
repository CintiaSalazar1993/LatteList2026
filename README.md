# LatteList – Backend

API REST de LatteList (Spring Boot 3.5, Java 21, MySQL).

## Requisitos
- Java 21
- MySQL 8 en ejecución
- Maven (o el wrapper `./mvnw`)

## Configuración
1. Crear una base de datos vacía (por ejemplo `lattelist`).
2. Crear el archivo local `src/main/resources/application-local.properties` (no se sube a GitHub) con:
   - `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`
   - `jwt.secret`, `jwt.expiration`
   - `admin.email`, `admin.password`
3. (Opcional) Cargar los datos de demostración con `sql/datos-lattelist.sql`.

## Ejecución
```
./mvnw spring-boot:run
```
## Pruebas
```
./mvnw test
```
## Documentación de la API
Con la aplicación en ejecución: Swagger UI en `/swagger-ui.html`.

## Notas
Cambiar los valores de ejemplo de administrador y clave JWT antes de cualquier uso real.
