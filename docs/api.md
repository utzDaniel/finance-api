# API

## Visão Geral

Este documento define os padrões para o design e implementação de APIs REST no projeto finance-api.

Ele garante consistência entre endpoints, convenções de nomenclatura, versionamento e documentação.

---

## Versionamento

Todas as APIs devem ser versionadas usando o caminho da URL.

---

### Padrão

 `/api/v{version}/<resource>`

 Examples:

- /api/v1/finances
- /api/v1/payroll

---

### Regras

- v1 → versão inicial
- v2+ → somente quando forem introduzidas alterações que quebrem a compatibilidade
- Alterações que não quebrem a compatibilidade NÃO devem criar uma nova versão

### Exemplos de alterações que causam incompatibilidade

- Remoção de campos da resposta
- Alteração de tipos de campo
- Renomeação de endpoints

### Exemplos de alterações não disruptivas

- Adição de novos campos
- Adição de novos endpoints

---

### HTTP Mapeamento de Métodos

| Method | Action | Example | Event |
|--------|--------|--------|------|
| POST | Create | /finances | CREATED |
| GET | Retrieve | /finances/{id} | RETRIEVED |
| GET | List | /finances | LISTED |
| PUT | Update | /finances/{id} | UPDATED |
| DELETE | Remove | /finances/{id} | DELETED |

---

## Convenções REST

### Nomenclatura de Recursos

- Use substantivos no plural
- Use letras minúsculas
- Use hífen somente quando tiver Camel Case

Exemplos:

- /finances
- /payroll

### Fonte

As regras de convenções estão definidas nesse link [Convenções](docs/conventions.md)

---

## Documentação (Swagger)

Todos os endpoints DEVEM ser documentados usando o Swagger (OpenAPI).

### Regras

- Cada controlador deve ser documentado
- Cada endpoint deve incluir:
    - resumo
    - descrição
    - esquema da requisição
    - esquema da resposta

### Fonte

As regras de documentação do Swagger estão definidas nesse link [Swagger](docs/swagger.md)