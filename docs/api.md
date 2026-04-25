# API REST

## Versionamento

- Padrão: `/api/v{version}/<resource>` — ex: `/api/v1/finances`
- Nova versão (`v2+`) somente em breaking change (remoção de campos, mudança de tipo, renomeação de endpoint)
- Adição de campos ou novos endpoints: não requer nova versão

## Métodos HTTP

| Método | Ação | Exemplo | Status | Evento |
|--------|------|---------|--------|--------|
| POST | Create | /finances | 201 Created | CREATED |
| GET | Retrieve | /finances/{id} | 200 OK | RETRIEVED |
| GET | List | /finances | 200 OK | LISTED |
| PUT | Update | /finances/{id} | 200 OK | UPDATED |
| DELETE | Remove | /finances/{id} | 204 No Content | DELETED |

## Nomenclatura de Recursos

- Substantivos no plural, letras minúsculas, hífen para camelCase
- Exemplos: `/finances`, `/payroll`

## Documentação

Todo endpoint DEVE ser documentado no OpenAPI — ver [Swagger](swagger.md)