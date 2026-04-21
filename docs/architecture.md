# Arquitetura - API de Finanças

## Visão Geral

A API de finanças é um serviço orientado à escrita, responsável por gerenciar dados financeiros e publicar eventos do domínio.

---

## Responsabilidades

- Gerenciar registros financeiros:
    - Receitas
    - Despesas fixas
    - Despesas variáveis
- Processar o registro da folha de pagamento
- Calcular o saldo mensal
- Publicar eventos financeiros
- Garantir a consistência dos dados

---

## Fluxo de Alto Nível

1. O cliente envia uma requisição HTTP
2. O controlador recebe a requisição
3. O serviço processa a lógica de negócios
4. O repositório persiste os dados
5. O serviço publica o evento
6. A resposta é retornada ao cliente

---

## Estilo de Arquitetura

- Arquitetura em Camadas
- Arquitetura Orientada a Eventos
- API REST

---

## Módulos

- finanças: 
    - Gerencia todas as operações financeiras.
- folha de pagamento
    - Gerencia os dados relacionados à folha de pagamento.

### Compartilhado

Utilitários comuns:
- Tratamento de exceções
- Modelos de resposta
- Utilitários

---

## Dependências externas

| Serviço    | Função                                      | Porta padrão |
|------------|---------------------------------------------|--------------|
| SQL Server | Persistência dos dados financeiros          | 1433         |
| ActiveMQ   | Broker de mensagens para eventos de domínio | 61616        |
| Keycloak   | Provedor de identidade (IdP) — emite JWT    | 8081         |

---

### Diagrama de dependências

      ┌──────────────────────────┐
      │       Cliente HTTP       │
      └────────────┬─────────────┘
                   │ Bearer JWT
                   ▼
      ┌──────────────────────────┐
      │       finance-api        │
      │  (Resource Server OAuth2)│
      └───┬──────────┬───────────┘
          │          │          │
     valida JWT   persiste   publica evento
          │          │          │
          ▼          ▼          ▼
     Keycloak    SQL Server  ActiveMQ
     :8081        :1433       :61616

---

## Segurança

- Servidor de Recursos OAuth2
- Validação JWT via Keycloak
- Todos os endpoints são protegidos por padrão

---

## Versionamento da API

- Versionamento baseado em URL:
    - /api/v1/...
Alterações futuras que quebrem a compatibilidade devem introduzir novas versões (v2, v3, etc).