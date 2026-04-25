# Segurança

## Modelo

Resource Server OAuth2 — valida JWT emitido pelo Keycloak. Não autentica usuários diretamente.

Fluxo: Cliente → Keycloak (obtém JWT) → finance-api (valida JWT e processa)

## Endpoints

| Endpoint | Acesso |
|----------|--------|
| /api/v1/finances | Autenticado |
| /api/v1/payroll | Autenticado |
| /api/v1/public/** | Público |
| /swagger-ui/** | Público |
| /actuator/health | Público |
| /actuator/info | Público |

## Erros de Autenticação

| Cenário | Status |
|---------|--------|
| Token ausente ou inválido | 401 |
| Token expirado | 401 |
| Acesso negado (escopo insuficiente) | 403 |

## Validação do Token

O token DEVE conter: `iss`, `sub`, `exp`, `iat`, `scope`  
A API valida: assinatura, expiração, emissor e audience (se configurado)

## Boas Práticas

- Nunca exponha dados sensíveis em respostas
- Sempre valide dados de entrada
- Use HTTPS em todos os ambientes

## Esquema OpenAPI

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