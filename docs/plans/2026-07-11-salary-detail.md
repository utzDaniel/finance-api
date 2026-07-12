# Plano: API de Detalhamento Salarial (finance-api)

Status: done

Autor: Daniel

Data: 2026-07-20

## TL;DR

Concluir o modulo `modules/salary` iniciado em `2026-06-20-api-resumo-salarial-inicial.md`. Implementar os endpoints CRUD de `/api/v1/finance/salary/{competenceDate}/detail` com paginacao, validacoes Jakarta, publicacao de eventos e testes unitarios de servico. Corrigir bugs nas entidades JPA existentes e completar os repositorios. A migration `V2__salary-detail.sql` ja existe e foi corrigida previamente.

## Dependencia

Continuacao de [2026-06-20-api-resumo-salarial-inicial.md](2026-06-20-api-resumo-salarial-inicial.md) — especificamente a Fase 7 (escopo iniciado e pendente).

## Contrato dos Endpoints

Todos os endpoints compartilham o mesmo response: lista paginada de `SalaryDetailResponse`.

```
GET    /api/v1/finance/salary/{competenceDate}/detail
POST   /api/v1/finance/salary/{competenceDate}/detail
PUT    /api/v1/finance/salary/{competenceDate}/detail
DELETE /api/v1/finance/salary/{competenceDate}/detail
```

### SalaryDetailResponse (campos)

| Campo    | Origem                      | Tipo       |
|----------|-----------------------------|------------|
| id       | salary_detail.id            | Long       |
| idType   | salary_detail_item_type.id  | Integer    |
| code     | salary_detail_item.code     | Integer    |
| name     | salary_detail_item.name     | String     |
| quantity | salary_detail.quantity      | Integer    |
| amount   | salary_detail.amount        | BigDecimal |

### Regras de negocio

- **POST**: faz upsert em `salary_detail_item` pelo `code` (cria se nao existe; se ja existe valida que o `name` informado e igual ao cadastrado, caso contrario rejeita com 400).
- **PUT**: valida que o `salary_detail.id` existe e pertence ao `user_id` autenticado (404 / 403). Faz upsert do item da mesma forma que o POST.
- **DELETE**: recebe `List<Long>` de IDs. Valida que todos pertencem ao `user_id` autenticado (403 se algum nao pertencer). Nao deleta `salary_detail_item` nem `salary_detail_item_type`.
- Paginacao padrao: `page=0`, `size=10`, ordenacao por `id` ascendente.

## Steps

### Fase 1 - Corrigir entidades JPA

1. Corrigir `SalaryDetailItemEntity.java` — alterar `@Table` para `salary_detail_item` (estava mapeado erroneamente para `salary_detail_item_type`); remover campo `code` que nao pertence a essa entidade; adicionar campo `code` correto.
2. Atualizar `SalaryDetailEntity.java` — substituir campos escalares `itemId` e `itemTypeId` por relacionamentos `@ManyToOne` para `SalaryDetailItemEntity` e `SalaryDetailItemTypeEntity` respectivamente.

### Fase 2 - Implementar repositorios

3. `SalaryDetailRepository` — estender `JpaRepository<SalaryDetailEntity, Long>` com:
   - `findAllByUserIdAndCompetenceDate(String, LocalDate, Pageable)` → `Page<SalaryDetailEntity>`
   - `findByIdAndUserId(Long, String)` → `Optional<SalaryDetailEntity>`
   - `findAllByIdInAndUserId(List<Long>, String)` → `List<SalaryDetailEntity>`
4. `SalaryDetailItemRepository` — estender `JpaRepository<SalaryDetailItemEntity, Integer>` com `findByCode(int)` → `Optional<SalaryDetailItemEntity>`.
5. `SalaryDetailItemTypeRepository` — estender `JpaRepository<SalaryDetailItemTypeEntity, Integer>`.

### Fase 3 - DTOs de request/response

6. `AddSalaryDetailRequest` — campos com validacoes Jakarta:
   - `idType: Integer` — `@NotNull`
   - `code: Integer` — `@NotNull`, `@Min(1)`
   - `name: String` — `@NotBlank`, `@Size(max = 50)`
   - `quantity: Integer` — `@NotNull`, `@Min(0)`
   - `amount: BigDecimal` — `@NotNull`, `@DecimalMin("0.00")`
7. `UpdateSalaryDetailRequest` — mesmos campos de `AddSalaryDetailRequest` mais:
   - `id: Long` — `@NotNull`
8. `DeleteSalaryDetailRequest` — campo:
   - `ids: List<Long>` — `@NotNull`, `@NotEmpty`
9. Atualizar `SalaryDetailResponse` — adicionar campo `id: Long` como primeiro campo do record.

### Fase 4 - Adicionar EventTypes

10. Adicionar em `EventType.java`:
    - `SALARY_DETAIL_ADDED`
    - `SALARY_DETAIL_UPDATED`
    - `SALARY_DETAIL_DELETED`

### Fase 5 - Implementar SalaryService

11. `getSalaryDetail(Jwt, LocalDate, Pageable)` → `Page<SalaryDetailResponse>` — busca os registros do usuario autenticado na competencia e mapeia para o response.
12. `addSalaryDetail(Jwt, LocalDate, AddSalaryDetailRequest)` — valida `idType` existente (404); upsert `salary_detail_item`; cria `salary_detail`; publica `SALARY_DETAIL_ADDED`; retorna page atualizada.
13. `updateSalaryDetail(Jwt, LocalDate, UpdateSalaryDetailRequest)` — busca `salary_detail` por `id` (404 se nao encontrado); valida posse (403 se `user_id` diferente); valida `idType` (404); upsert item; atualiza registro; publica `SALARY_DETAIL_UPDATED`; retorna page.
14. `deleteSalaryDetail(Jwt, LocalDate, DeleteSalaryDetailRequest)` — busca todos os IDs informados; 404 se algum nao existir; 403 se algum nao pertencer ao usuario; deleta; publica `SALARY_DETAIL_DELETED`; retorna page.

### Fase 6 - Atualizar SalaryController

15. Atualizar metodo GET para retornar `Page<SalaryDetailResponse>` aceitando parametros de paginacao via `Pageable`.
16. Adicionar `POST /api/v1/finance/salary/{competenceDate}/detail` com `@RequestBody @Valid AddSalaryDetailRequest`.
17. Adicionar `PUT /api/v1/finance/salary/{competenceDate}/detail` com `@RequestBody @Valid UpdateSalaryDetailRequest`.
18. Adicionar `DELETE /api/v1/finance/salary/{competenceDate}/detail` com `@RequestBody @Valid DeleteSalaryDetailRequest`.

### Fase 7 - Testes unitarios de servico

19. `SalaryServiceTest` — cenarios:
    - `getSalaryDetail` — pagina vazia; pagina com registros mapeados corretamente.
    - `addSalaryDetail` — item novo criado; item existente com nome igual (reutiliza); item existente com nome diferente (400); idType nao encontrado (404); evento publicado.
    - `updateSalaryDetail` — id nao encontrado (404); id de outro usuario (403); idType nao encontrado (404); atualizacao bem-sucedida; evento publicado.
    - `deleteSalaryDetail` — id nao encontrado (404); id de outro usuario (403); delecao bem-sucedida; evento publicado.

### Fase 8 - Atualizar spec.json

20. Adicionar tag `"Salary"` na secao `tags`.
21. Adicionar path `GET /api/v1/finance/salary/{competenceDate}/detail` com response paginado.
22. Adicionar path `POST /api/v1/finance/salary/{competenceDate}/detail` com schema `AddSalaryDetailRequest`.
23. Adicionar path `PUT /api/v1/finance/salary/{competenceDate}/detail` com schema `UpdateSalaryDetailRequest`.
24. Adicionar path `DELETE /api/v1/finance/salary/{competenceDate}/detail` com schema `DeleteSalaryDetailRequest`.
25. Adicionar schemas: `SalaryDetailResponse`, `SalaryDetailPageResponse`, `AddSalaryDetailRequest`, `UpdateSalaryDetailRequest`, `DeleteSalaryDetailRequest`.
