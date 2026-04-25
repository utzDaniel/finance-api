---
mode: agent
description: Lê um plano de execução em docs/plans/, implementa as tarefas descritas e atualiza docs/plans/README.md ao final.
---

Execute o plano de desenvolvimento indicado abaixo, seguindo todas as convenções da finance-api.

## Plano a executar

${input:plano:Caminho do arquivo do plano (ex: docs/plans/2026-04-19-infra-setup.md)}

## Passos obrigatórios

1. **Ler o plano** — abrir o arquivo indicado e identificar:
   - Objetivo e contexto
   - Tarefas listadas e sua ordem
   - Dependências entre tarefas

2. **Verificar pré-condições** — confirmar que o ambiente está pronto (infra, dependências Maven)

3. **Implementar cada tarefa** na ordem definida no plano, aplicando:
   - [Arquitetura](../../docs/architecture.md) — camadas e responsabilidades
   - [Convenções](../../docs/conventions.md) — nomenclatura de classes e pacotes
   - [API REST](../../docs/api.md) — versionamento e mapeamento HTTP
   - [Eventos](../../docs/events.md) — publicação em operações de escrita
   - [Segurança](../../docs/security.md) — proteção de endpoints
   - [Swagger](../../docs/swagger.md) — documentação OpenAPI obrigatória
   - [Testes](../../docs/testing.md) — testes unitários para cada nova funcionalidade

4. **Rodar testes** após cada tarefa implementada:
   ```
   mvn clean test
   ```
   Todos os testes existentes devem continuar passando.

5. **Atualizar `docs/plans/README.md`**:
   - Alterar o status do plano para `done`
   - Garantir que a entrada inclua: nome do arquivo, descrição resumida e status

Ao final, apresente um resumo do que foi implementado e liste os arquivos criados ou modificados.
