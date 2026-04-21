# Swagger / OpenAPI

## Visão Geral

Este projeto segue uma abordagem de documentação de API baseada no contrato, utilizando o OpenAPI 3.

A documentação Swagger é considerada a **fonte da verdade** para o contrato da API.

---

## Padrão de Documentação

A documentação da API deve seguir um formato estruturado e consistente.

---

## Estrutura OpenAPI

Toda API deve definir:

- info → Metadados da API
- servers → Ambientes
- tags → Endpoints agrupados
- paths → Endpoints
- components → Esquemas reutilizáveis

---

## Exemplo de Estrutura Básica

```yaml
openapi: 3.0.0

info:
  title: API Financeira
  version: "1.0.0"
  description: API para gestão financeira

servers:
  - url: http://localhost:8080

tags:
  - name: Finanças
    description: Operações financeiras

paths: 
  /api/v1/finances:
    post:
      tags:
        - Finanças
      summary: Cria um registro financeiro
      description: Cria um novo lançamento financeiro e publica um evento

```

---

## Todos os esquemas devem ser reutilizáveis ​​por meio de componentes.

```yaml
components:
  schemas:
    FinancesCreateRequest:
      type: object
      properties:
        description:
          type: string
        amount:
          type: number
```

---

## Regras do esquema

- Nunca duplique definições de esquema
- Sempre use $ref
- Mantenha os esquemas reutilizáveis ​​e modulares

---

## Exemplo com $ref

```yaml
requestBody:
  content:
    application/json:
      schema:
        $ref: '#/components/schemas/FinancesCreateRequest'`
```

---

## Padrão de resposta

```yaml
responses:
  '200':
    description: Success
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/FinancesCreatedDTO'
```

---

## Tags Organização

Cada módulo deve ter sua própria etiqueta:

- Finances


---

## Contrato da API (spec.json)

O arquivo `docs/openapi/spec.json` é a **especificação OpenAPI oficial** do projeto.

- Ele deve ser mantido em sincronia com os endpoints implementados
- Toda vez que um endpoint for criado, alterado ou removido, o `spec.json` deve ser atualizado
- O arquivo é renderizado em `docs/openapi/spec.html` para visualização estática

Localização: `docs/openapi/spec.json`

---

## Convenções de nomenclatura

As regras de Convenções de nomenclatura estão definidas nesse link [Convenções](docs/conventions.md)

---

## Segurança

A segurança deve ser definida globalmente:

```yaml
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

security:
  - bearerAuth: []
```

---

## Controle de versão

- O caminho deve incluir a versão: /api/v1
- A versão OpenAPI deve seguir o versionamento semântico

---

## Cada Endpoint DEVE incluir exemplos.

Exemplo - Create Finance

```yaml
paths:
  /api/v1/finances:
    post:
      tags:
        - Finances
      summary: Create finance
      description: Creates a new finance record

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
        '200':
          description: Finance created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/FinancesCreatedDTO'
              example:
                id: 1
                description: Salary
                amount: 5000
```

---

## Princípios-chave (estilo Pix)

- A documentação da API é um contrato, não apenas um guia.
- Tudo deve ser definido explicitamente.
- A reutilização por meio de componentes é obrigatória.
- Exemplos devem sempre estar presentes.
- A nomenclatura deve ser consistente em todo o sistema.

---

## Antipadrões (Evitar)

- Esquemas embutidos (componentes sem)
- Exemplos ausentes
- Descrições genéricas
- Misturar lógica de negócios na documentação

---