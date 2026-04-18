# API Financeira

## Descrição

A API Financeira é um serviço RESTful para gestão financeira pessoal.

Ela permite o acompanhamento de receitas, despesas, dados de folha de pagamento e publica eventos para integração com outros serviços.

---

## Tecnologias

- Java 21
- Spring Boot
- Maven
- SQL Server
- ActiveMQ
- Flyway
- OAuth2 (Keycloak)
- OpenAPI (SpringDoc)
- Flyway

---

## Funcionalidades

- Gerenciamento de registros financeiros:
    - Receitas
    - Despesas fixas
    - Despesas variáveis
- Registro de folha de pagamento
- Cálculo do saldo mensal
- Publicação de eventos para integração
- API segura com OAuth2

---

## Documentação Técnica

- [Arquitetura](docs/architecture.md)
- [API REST](docs/api.md)
- [Eventos](docs/events.md)
- [Segurança](docs/security.md)
- [Swagger](docs/swagger.md)
- [Testes](docs/testing.md)
- [Convenções](docs/conventions.md)

---

## Documentação da API

A documentação da API é gerada a partir da especificação OpenAPI.

### Referência Principal

👉 [Documentação OpenAPI (HTML)](docs/openapi/spec.html)

---

### Acesso Adicional

- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

---

## Regras da Documentação

- O arquivo `spec.html` é a principal fonte para exploração da API
- Ele é gerado a partir do arquivo `spec.json`
- Deve ser sempre atualizado após alterações na API

---

## Gerar Documentação

Use:

```bash
Comandos.bat
```
Option:

4 - Gerar Documentacao

---