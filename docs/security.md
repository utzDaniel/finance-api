# Segurança

## Visão Geral

A API de finanças utiliza OAuth2 com tokens JWT para autenticação e autorização.
A autenticação é delegada a um Provedor de Identidade (IdP) externo, utilizando o Keycloak.
Este documento define o modelo de segurança, as regras de validação de tokens e os padrões de acesso.

---

## Modelo de Segurança

A API atua como um **Servidor de Recursos**.

Responsabilidades:

- Validar tokens JWT
- Aplicar controle de acesso
- Proteger endpoints

A API NÃO:

- Autentica usuários diretamente
- Emite tokens

---

## Fluxo de Autenticação

1. O cliente se autentica com o Provedor de Identidade (Keycloak)
2. O cliente recebe um token de acesso (JWT)
3. O cliente envia o token no cabeçalho da requisição:
    - Authorization: Bearer `<access_token>`
4. API validates token
5. Request is processed if valid

---

## Configuração do OAuth2

- Protocolo: OAuth2
- Tipo de token: Token de portador (Bearer Token)
- Formato: JWT

---

## Requisitos do Token

Todos os tokens recebidos DEVEM:

- Ser assinados por uma autoridade emissora confiável
- Ser válidos (não expirados)
- Conter as declarações obrigatórias

---

## Declarações Obrigatórias

| Declaração | Descrição |
|------|-------------|
| iss | Emissor do token |
| sub | Assunto (ID do usuário/cliente) |
| exp | Tempo de expiração |
| iat | Emitido em |
| scope | Permissões concedidas |

---

## Autorização

Todos os endpoints são protegidos por padrão.

Endpoints públicos devem ser definidos explicitamente.

---

## Controle de Acesso

O controle de acesso é baseado em:

- escopos (OAuth2)
- funções (opcional)

---

### Exemplos de Funções

- USUÁRIO
- ADMINISTRADOR

---

## Proteção de Endpoint

| Endpoint | Acesso |
|--------|--------|
| /api/v1/finances | Autenticado |
| /api/v1/payroll | Autenticado |
| /api/v1/public/** | Público |

---

## Regras de Validação de Token

A API DEVE validar:

- Assinatura
- Expiração (exp)
- Emissor (iss)
- Público-alvo (aud) (se configurado)

---

## Tratamento de Erros

| Cenário | Resposta |
|--------|---------|
| Token ausente | 401 Não autorizado |
 | Token inválido | 401 Não autorizado |
| Token expirado | 401 Não autorizado |
| Acesso negado | 403 Proibido |

---

## Documentação

As regras de documentação de segurança estão definidas nesse link [Swagger](docs/swagger.md)

---

## Exemplo de esquema de segurança

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

## Boas Práticas

- Nunca exponha dados sensíveis em respostas
- Sempre valide os dados de entrada
- Não confie em dados fornecidos pelo cliente
- Use HTTPS em todos os ambientes
- Mantenha os tokens com curta duração

## Escopos

Os escopos definem as permissões concedidas a um cliente ou usuário.

Eles são incluídos no token JWT e usados ​​pela API para controlar o acesso aos recursos.

---

## Convenção de Nomenclatura de Escopos

Os escopos devem seguir o padrão:

- `<resource>.<action>`

Onde:

- recurso → recurso API (por exemplo, finanças, folha de pagamento)
- ação → operação (ler, escrever, excluir)

---

## Escopos padrão

### Finanças

- finance.read → leitura de dados financeiros
- finance.write → permite criação e atualização

---

## Mapeamento de escopo

| Método HTTP | Endpoint  | Scope |
|------------|---------|-------|
| GET | /api/v1/finances/{id} | finance.read |
| GET | /api/v1/finances | finance.read |
| POST | /api/v1/finances | finance.write |
| PUT | /api/v1/finances/{id} | finance.write |

---

## Uso na API

Os escopos são validados pela API usando o Spring Security.

Os endpoints devem declarar explicitamente os escopos necessários.

---

## Observabilidade (Opcional)

- Eventos de segurança podem ser registrados:
    - falhas de autenticação
    - falhas de autorização
    - padrões de acesso suspeitos

---

## Exemplos

### Exemplo 1 - Solicitação autenticada

`GET /api/v1/finances/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

### Exemplo 2 - Token ausente

Request:

`GET /api/v1/finances/1`

Response:

`401 Unauthorized`

### Exemplo 3 - Token inválido

Request:

`GET /api/v1/finances/1
Authorization: Bearer invalid_token`

Response: 

`401 Unauthorized`

### Exemplo 4 - Acesso negado sem scope para finance.write

Request:

`DELETE /api/v1/finances/1
Authorization: Bearer valid_token_without_permission`

Response:

`403 Forbidden`

### Exemplo 5 - JWT Payload com scopes (decodificada)

```json
{
  "iss": "http://localhost:8080/realms/finance",
  "sub": "123456",
  "exp": 1710000000,
  "iat": 1709990000,
  "scope": "finance.read finance.write"
}
```
