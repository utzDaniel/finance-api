# Plano: Infra setup

Status: in-progress

Autor: Daniel

Data: 2026-04-19

TL;DR

Gerar `pom.xml` (Java 21, Spring Boot 4.0.5), estrutura Maven, classe `Application`, `application.yml`, Flyway; criar `Dockerfile` + `docker-compose` com `sqlserver`, `activemq` e `keycloak` para desenvolvimento.

Steps 
1. Criar `pom.xml` criado e dependências adicionadas
   - Parent: `spring-boot-starter-parent`
   - Dependências: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `mssql-jdbc`, `spring-boot-starter-activemq`, `flyway-core`, `springdoc-openapi-starter-webmvc-ui`, `spring-boot-starter-security`, `keycloak-spring-boot-starter`, `lombok`, `spring-boot-starter-test`.
2. Estrutura
   - `src/main/java/br/com/finance`
   - `src/main/resources`
   - `src/main/resources/db/migration`
   - `src/test/java/br/com/finance`
3. Artefatos iniciais:
   - `Application.java` (`@SpringBootApplication`)
   - `application.yml` (placeholders para DB, ActiveMQ, Keycloak)
   - `db/migration/V1__init.sql`
   - `ApplicationTests.java`
4. Conteinerização implementada
   - `Dockerfile` (multi-stage) — removida dependência de `mvnw`, usa Maven da imagem de build
   - `docker-compose.yml` com serviços: `sqlserver`, `activemq`, `keycloak`, `app`
   - `docker/keycloak/realm-export.json` de exemplo
5. Verificações realizadas/observadas
   - `mvn -DskipTests package` gerou `target/finance-api-0.1.0-SNAPSHOT.jar` com sucesso
   - `docker compose up` inicializou `sqlserver`, `activemq`, `keycloak`; `app` exigiu ajustes (DB, Flyway) que foram tratados iterativamente
   - Flyway: migration `V1__init.sql` ajustada para executar dentro do database `finance` (usa `EXEC('USE [finance]; ...')`).

Relevant files (criadas/atualizadas)
- `pom.xml` (Spring Boot 4.0.5)
- `src/main/java/br/com/finance/Application.java`
- `src/main/resources/application.yml`
- `src/main/resources/db/migration/V1__init.sql` (idempotente, cria DB/schema/table)
- `src/test/java/br/com/finance/ApplicationTests.java`
- `Dockerfile` (multi-stage, usa Maven na imagem)
- `docker-compose.yml` (serviços: sqlserver, activemq, keycloak, app)
- `docker/keycloak/realm-export.json`

Decisions / Assumptions
- Empacotamento: `jar` Spring Boot
- Banco: SQL Server
- Broker: ActiveMQ
- Auth: Keycloak
- Java 21
- Lombok incluso por conveniência

Next actions
- Implementar arquivos listados.