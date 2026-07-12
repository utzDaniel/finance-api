# Plans (docs/plans)

Este diretório contém planos de execução do projeto (arquivos Markdown).

Status key: `draft`, `approved`, `in-progress`, `done`

Planos disponíveis
- [Infra setup](2026-04-19-infra-setup.md) — Plano inicial para infraestrutura e scaffold do projeto
- [Security, Swagger, ActiveMQ e Actuator](2026-04-21-infra-config.md) — Configuração do Resource Server OAuth2 JWT, Swagger público, ActiveMQ, Actuator e realm-export `status: done`
- [API Resumo Salarial — Entrega Inicial](2026-06-20-api-resumo-salarial-inicial.md) — Endpoints `GET/PUT /summary/{competenceDate}`, persistência de salários, eventos e testes `status: done`
- [API Detalhamento Salarial](2026-07-11-salary-detail.md) — Endpoints CRUD `GET/POST/PUT/DELETE /salary/{competenceDate}/detail`, paginação, validações, eventos e testes `status: done`

Como usar
- Crie/edite planos e abra PRs para revisão.
- Nomeie planos com data ou número para ordenação (ex: `2026-04-19-infra-setup.md`).