# Instruções do Copilot - API de Finanças

## Idioma (OBRIGATÓRIO)

- Todas as respostas DEVEM ser em português (pt-BR)
- Termos técnicos podem permanecer em inglês

---

## Stack

- **Java 21** + **Spring Boot 4.0.6**
- **SQL Server** como banco de dados principal
- **Flyway** para migrações (apenas SQL Server; testes usam H2)
- **ActiveMQ** para eventos assíncronos (fila `events`)
- **Keycloak** como servidor de identidade (OAuth2 JWT / Resource Server)
- **Lombok** nas entidades JPA
- **Validation API** (`@Valid`, `@Validated`) nos controllers
