---
mode: agent
description: Cria um novo recurso completo (Controller, Service, Repository, DTOs, evento, testes e documentação OpenAPI).
---

Crie um novo recurso REST completo para a finance-api seguindo todas as convenções do projeto.

## Recurso a criar

${input:recurso:Nome do recurso (ex: Expense, Income, Payroll)}

## Passos obrigatórios

Siga exatamente esta ordem:

1. **Controller** — `br.com.finance.modules.<recurso_lower>.controller.<Recurso>Controller`
   - Endpoints REST com versionamento `/api/v1/<recurso_plural>`
   - Seguir [API REST](../../docs/api.md) e [Convenções](../../docs/conventions.md)

2. **Service** — `br.com.finance.modules.<recurso_lower>.service.<Recurso>Service`
   - Lógica de negócio
   - Publicar evento após toda operação de escrita (POST/PUT/DELETE) → [Eventos](../../docs/events.md)

3. **Repository** — `br.com.finance.modules.<recurso_lower>.repository.<Recurso>Repository`
   - Apenas persistência JPA

4. **Entity** — `br.com.finance.modules.<recurso_lower>.entity.<Recurso>`
   - Mapeamento JPA com SQL Server

5. **DTOs**
   - Request: `<Recurso>CreateRequest`, `<Recurso>UpdateRequest`
   - Response: `<Recurso>DTO`, `<Recurso>CreatedDTO`, `<Recurso>UpdatedDTO`

6. **Migração Flyway** — próximo arquivo em `src/main/resources/db/migration/` (ex: `V2__create_<recurso>.sql`)

7. **Documentação OpenAPI** — anotar controller com `@Operation`, `@ApiResponse` e `@Schema` em todos os DTOs
   - Seguir [Swagger](../../docs/swagger.md)

8. **Testes unitários** — `src/test/java/br/com/finance/modules/<recurso_lower>/service/<Recurso>ServiceTest.java`
   - JUnit 5 + Mockito, mockar todas as dependências
   - Cobrir: criação, atualização, deleção, busca, erros e publicação de evento
   - Padrão de nome: `<metodo>Should<comportamentoEsperado>`
   - Seguir [Testes](../../docs/testing.md)

9. **Rodar testes** — `mvn clean test` — todos devem passar sem regressões

Ao final, liste os arquivos criados/modificados.
