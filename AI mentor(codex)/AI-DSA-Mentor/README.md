# AI-DSA-Mentor

Spring Boot 3 foundation for an AI-powered DSA mentor API.

## Stack

- Java 21
- Spring Boot 3
- Spring Security
- JWT authentication foundation
- MySQL
- Spring Data JPA
- Lombok
- Maven
- Swagger/OpenAPI

## Local Setup

Set the required environment variables before running in production-like environments:

```bash
DB_URL=jdbc:mysql://localhost:3306/ai_dsa_mentor
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=replace-with-a-strong-256-bit-secret
```

Run the app:

```bash
mvn spring-boot:run
```

Swagger UI is available at:

```text
http://localhost:8080/api/v1/swagger-ui.html
```
