# API Financeira

API REST de gestão financeira pessoal — receitas, despesas, folha de pagamento e publicação de eventos para integração.

## Stack

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| SQL Server | Banco de dados |
| Flyway | Migrações SQL Server |
| ActiveMQ | Eventos assíncronos |
| Keycloak | Servidor de identidade (OAuth2/JWT) |

## Pré-requisitos

Os serviços abaixo devem estar rodando **antes** de iniciar a aplicação:

- **SQL Server** — `localhost:1433` (banco `ficance`)
- **ActiveMQ** — `tcp://localhost:61616`
- **Keycloak** — `http://localhost:9999` (realm `development`)

## Arquitetura

```
src/main/java/br/com/finance/
├── config/          # Segurança, Keycloak, tratamento de erros, ActiveMQ
```

**Eventos** são publicados após toda mutação (create/update/delete) na fila `events` do ActiveMQ.

## Banco de dados

Migrações gerenciadas pelo Flyway em `src/main/resources/db/migration/` (`V{n}__descricao.sql`).

## Autenticação

Todos os endpoints protegidos exigem um **Bearer Token JWT** obtido via Keycloak

```
Authorization: Bearer <token>
```

Roles aceitas: `FINANCE`, `ADMIN` (extraídas de `realm_access.roles` no JWT).

Endpoints públicos: `/actuator/health`, `/actuator/info`, `/api/v1/public/**`.

---
## Documentação da API

A especificação OpenAPI completa está em [docs/openapi/spec.html](docs/openapi/spec.html).

