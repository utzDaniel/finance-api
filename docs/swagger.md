# Swagger / OpenAPI

## Regras

- `docs/openapi/spec.json` é o contrato oficial da API (fonte da verdade)
- Todo endpoint DEVE ter: summary, description, request schema, response schema e exemplos
- Schemas devem ser reutilizáveis via `$ref` em `components/schemas`
- Nunca duplique definições de schema
- Gerar `spec.html` após alterações: executar `scripts/generate-docs.ps1`

## Estrutura Obrigatória

```yaml
openapi: 3.0.0
info: { title, version, description }
servers: [ { url } ]
tags: [ { name, description } ]
paths: { ... }
components: { schemas: { ... }, securitySchemes: { bearerAuth: ... } }
security: [ { bearerAuth: [] } ]
```

## Exemplo de Endpoint

```yaml
paths:
  /api/v1/finances:
    post:
      tags: [Finances]
      summary: Cria um registro financeiro
      description: Cria um novo lançamento e publica o evento FINANCE_CREATED
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/FinancesCreateRequest'
            example:
              description: Salary
              amount: 5000
      responses:
        '201':
          description: Registro criado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/FinancesCreatedResponse'
              example:
                id: 1
                description: Salary
                amount: 5000
        '401':
          description: Não autorizado
```

## Antipadrões

- Schemas embutidos inline (sem `$ref`)
- Exemplos ausentes