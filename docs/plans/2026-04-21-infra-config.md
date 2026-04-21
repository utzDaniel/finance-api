# Plano: Security, Swagger, ActiveMQ e Actuator

Status: completed

Autor: Daniel

Data: 2026-04-21

## TL;DR

Configurar o projeto como Resource Server OAuth2 JWT via Keycloak (substituindo o adapter deprecated),
expor o Swagger UI publicamente sem autenticação, corrigir a configuração do ActiveMQ, habilitar o
Actuator e atualizar o `realm-export.json` e `spec.json`.

O problema atual: sem uma `SecurityConfig`, o Spring Security ativa o form login por padrão, causando
redirect para página de login ao acessar o Swagger.

---

## Steps

### Phase 1 — Dependências (`pom.xml`)

1. Remover `keycloak-spring-boot-starter` (incompatível com Spring Boot 3+/4+, deprecado pelo Keycloak)
2. Adicionar `spring-boot-starter-oauth2-resource-server` (suporte nativo do Spring Security para JWT)
3. Adicionar `spring-boot-starter-actuator` (ausente no `pom.xml`, mas referenciado no `application.yml`)

### Phase 2 — `application.yml`

4. Corrigir namespace do ActiveMQ: `activemq.broker-url` → `spring.activemq.broker-url`
5. Corrigir namespace do Flyway: `flyway:` raiz → `spring.flyway:`
6. Remover bloco `keycloak:` e adicionar:
   - `spring.security.oauth2.resourceserver.jwt.issuer-uri: ${KEYCLOAK_URL}/realms/${KEYCLOAK_REALM:finance-dev}`
7. Adicionar config do Springdoc:
   - `springdoc.swagger-ui.path: /swagger-ui/index.html`
   - `springdoc.api-docs.path: /v3/api-docs`
8. Expandir config do Actuator: expor `health`, `info`, `metrics` com `show-details: always`

### Phase 3 — `SecurityConfig.java` *(nova classe)*

9. Criar `br.com.finance.config.SecurityConfig` com:
   - Form login e HTTP Basic desabilitados (elimina o redirect para página de login)
   - OAuth2 Resource Server com validação JWT
   - `JwtAuthenticationConverter` com `setAuthorityPrefix("")` para authorities sem prefixo `SCOPE_`
   - **Rotas públicas**: `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health`, `/actuator/info`, `/api/v1/public/**`
   - **Autorização por scope** conforme `docs/security.md`:
     - `GET /api/v1/finances/**` → `hasAuthority("finance.read")`
     - `POST /api/v1/finances` → `hasAuthority("finance.write")`
     - `PUT /api/v1/finances/**` → `hasAuthority("finance.write")`
     - `DELETE /api/v1/finances/**` → `hasAuthority("finance.write")`
   - `AuthenticationEntryPoint` customizado → retorna `401 Unauthorized` em JSON (sem redirect)
   - `AccessDeniedHandler` customizado → retorna `403 Forbidden` em JSON

### Phase 4 — `OpenApiConfig.java` *(nova classe)*

10. Criar `br.com.finance.config.OpenApiConfig` com:
    - Metadados: título `API Financeira`, versão `1.0.0`, descrição
    - `SecurityScheme` do tipo `bearerAuth` (Bearer JWT) conforme `docs/swagger.md`
    - URL do servidor (`http://localhost:8080`)

### Phase 5 — `ActiveMQConfig.java` *(nova classe)*

11. Criar `br.com.finance.config.ActiveMQConfig` com:
    - `ActiveMQConnectionFactory` configurado com `spring.activemq.broker-url`
    - `JmsTemplate` com `MappingJackson2MessageConverter` (JSON para eventos de domínio)
    - `DefaultJmsListenerContainerFactory` para futuros consumers

### Phase 6 — `realm-export.json` *(atualizar)*

12. Adicionar `clientScopes` para `finance.read`, `finance.write`:
    - Mapper de protocolo para incluir os valores no claim `scope` do JWT
    - Associar ao client `finance-client` via `optionalClientScopes`

### Phase 7 — `spec.json` *(atualizar)*

13. Preencher `docs/openapi/spec.json` (atualmente vazio) com a spec OpenAPI completa:
    - `info`, `servers`, `tags`
    - `paths` para endpoints de `/api/v1/finances` (GET, POST, PUT, DELETE)
    - `components.securitySchemes.bearerAuth` (Bearer JWT)
    - `security: [{bearerAuth: []}]` global
    - Escopos documentados por endpoint conforme tabela em `docs/security.md`

---

## Relevant files

- `pom.xml`
- `src/main/resources/application.yml`
- `src/main/java/br/com/finance/config/SecurityConfig.java` (criar)
- `src/main/java/br/com/finance/config/OpenApiConfig.java` (criar)
- `src/main/java/br/com/finance/config/ActiveMQConfig.java` (criar)
- `docker/keycloak/realm-export.json`
- `docs/openapi/spec.json`

---

## Verification

1. `mvn clean package -DskipTests` — build compila sem erros
2. `docker-compose up` → `http://localhost:8080/swagger-ui/index.html` abre sem página de login
3. `GET http://localhost:8080/actuator/health` → `200 {"status":"UP"}`
4. `GET http://localhost:8080/api/v1/finances` sem token → `401 JSON` (sem redirect 302)
5. `GET http://localhost:8080/api/v1/finances` com token + scope `finance.read` → `200`
6. `POST http://localhost:8080/api/v1/finances/1` com token sem `finance.read` → `403 JSON`
7. Keycloak: gerar token com scope `finance.read` e validar claim `scope` no JWT decodificado

---

## Decisions / Assumptions

- Keycloak adapter substituído pelo `spring-boot-starter-oauth2-resource-server` nativo — confirmado
- Authorities sem prefixo `SCOPE_`: via `JwtAuthenticationConverter.setAuthorityPrefix("")`
- Actuator público: apenas `/actuator/health` e `/actuator/info`
- Swagger UI totalmente público — sem autenticação para visualizar a documentação
- `spec.json` é a fonte de verdade para o contrato da API

## Next actions

- Implementar todas as fases acima