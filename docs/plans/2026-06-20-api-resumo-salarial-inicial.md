# Plano: Primeira Entrega da API para Tela Inicial de Financas (finance-api)

Status: partially-implemented

Autor: Copilot

Data: 2026-07-05

## TL;DR

Os changes atuais implementam o fluxo principal da tela inicial via endpoint unico de resumo mensal com consulta e atualizacao do salario do usuario em `/api/v1/finance/summary/{competenceDate}`. A entrega tambem adiciona leitura de usuario/familia no banco do Keycloak, persistencia de salarios no banco de financas, atualizacao de contrato OpenAPI e publicacao de evento de atualizacao. O modulo de detalhamento salarial (`/salary/{competenceDate}/detail`) foi apenas iniciado e ainda nao esta concluido.

## Steps

### Fase 1 - Estrutura implementada de resumo mensal

1. Modulo `modules/summary` criado com `SummaryController`, `SummaryService`, `SalaryRepository` e DTOs de request/response.
2. DTO `MonthlySummaryResponse` implementado com arredondamento monetario para duas casas.
3. DTO `UpdateMonthlySummaryRequest` implementado com validacoes `@NotNull` e `@DecimalMin`.
4. `TimestampUtils` passou a centralizar regex e parse de `competenceDate` no formato `yyyy-MM-dd`, normalizando a competencia para o primeiro dia do mes.

### Fase 2 - Persistencia e acesso a dados

5. Migration `V1__init.sql` criada para tabela `salary` com unicidade por `user_id + competence_date`, checks de valores nao negativos e indices por usuario e competencia.
6. `SalaryEntity` mapeada em JPA para a tabela `dbo.salary` com timestamps UTC em `@PrePersist` e `@PreUpdate`.
7. `SalaryRepository` implementado com busca por usuario/competencia e agregacao de salarios familiares por competencia.
8. Configuracoes `FinanceDatabaseConfig` e `KeycloakDatabaseConfig` adicionadas para separar o datasource principal do datasource de leitura do Keycloak.

### Fase 3 - Regra de negocio implementada

9. `KeycloakService` e `KeycloakReadRepository` implementados para localizar o usuario autenticado por `preferred_username`, buscar familia e listar membros da familia no banco do Keycloak.
10. `SummaryService#getMonthlySummary` implementado para retornar nome, sobrenome, nome da familia, salario bruto/liquido do usuario e agregados da familia.
11. Quando nao existe registro de salario para a competencia, o servico retorna valores zerados para o usuario.
12. `SummaryService#updateMonthlySummary` implementado para criar ou atualizar o salario do usuario autenticado na competencia informada.
13. Regra de negocio implementada para rejeitar payloads em que `userNetSalary` seja maior que `userGrossSalary`.

### Fase 4 - Endpoints REST entregues

14. `GET /api/v1/finance/summary/{competenceDate}` implementado para consultar o resumo mensal.
15. `PUT /api/v1/finance/summary/{competenceDate}` implementado para atualizar salario bruto/liquido do usuario e retornar o resumo consolidado.
16. `GlobalExceptionHandler`, `Violacao` e `TimestampUtils` foram ajustados para tratar melhor erros de payload, path param e respostas padronizadas.

### Fase 5 - Seguranca e observabilidade utilizadas

17. O fluxo usa o `Jwt` autenticado para identificar o usuario consultado e atualizado.
18. A autorizacao continua delegada ao `SecurityConfig` existente; os changes desta entrega nao alteram as regras de roles.
19. Evento `SALARY_SUMMARY_UPDATED` foi adicionado em `EventType` e publicado apos atualizacao bem-sucedida do resumo mensal.

### Fase 6 - Contrato e testes implementados

20. `docs/openapi/spec.json` e `docs/openapi/spec.html` foram atualizados com o contrato de `GET/PUT /summary/{competenceDate}`.
21. Testes unitarios foram adicionados para `SummaryService`, cobrindo usuario inexistente, resumo sem familia, agregacao familiar, validacao de salario liquido maior que bruto e publicacao de evento.
22. Testes de `GlobalExceptionHandler`, `TimestampUtils` e `Violacao` foram ajustados para o novo comportamento de validacao e formatacao.

### Fase 7 - Escopo iniciado e ainda pendente

23. O modulo `modules/salary` foi iniciado com `SalaryController`, `SalaryService`, DTOs e migration `V2__salary-detail.sql` para detalhamento salarial.
24. O endpoint planejado `GET /api/v1/finance/salary/{competenceDate}/detail` ainda nao esta concluido: `SalaryService#getSalaryDetail` retorna `null` e `SalaryDetailRepository` ainda esta vazio.
25. A migration `V2__salary-detail.sql` existe, mas ainda precisa revisao e fechamento antes de ser considerada pronta para uso.
26. Nao ha testes de controller/integracao para os endpoints novos nesta entrega.