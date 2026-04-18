# Eventos

## Visão Geral

O sistema publica eventos de domínio após operações em recursos financeiros.

Os eventos são usados ​​para integração com outros serviços, auditoria e observabilidade.

---

## Convenção de Nomenclatura

Os eventos seguem o padrão:
 - `<RESOURCE>_<ACTION>`

Onde:
- RESOURCE → representa o recurso da API (ex.: FINANCE, PAYROLL)
- ACTION → representa a operação realizada


---

## Tipos de Eventos

### Operações de Escrita (Eventos de Domínio)

- FINANCE_CREATED → POST /api/v1/finances
- FINANCE_UPDATED → PUT /api/v1/finances/{id}
- FINANCE_DELETED → DELETE /api/v1/finances/{id}
- PAYROLL_REGISTERED → POST /api/v1/payroll

---

### Operações de Leitura (Opcional - Eventos de Observabilidade)

Esses eventos são opcionais e devem ser usados ​​para:

- registro de auditoria
- análise
- monitoramento

Exemplos:

- FINANCE_RETRIEVED → GET /api/v1/finances/{id}
- FINANCE_LISTED → GET /api/v1/finances

---

## Regras

- Os eventos devem ser publicados somente pela camada de serviço
- Toda operação de escrita DEVE publicar um evento
- Eventos de leitura são opcionais e não devem impactar o desempenho
- Os eventos devem ser imutáveis

---

## Campos Padrão

Todos os eventos devem conter:

- id → identificador do recurso
- type → tipo do evento
- timestamp → hora de criação do evento em time zone UTC
- userId → usuário autenticado

Opcional:

- payload → dados adicionais

---

## Broker

- ActiveMQ

## Estrutura do Evento

Exemplo de estrutura:

```json
{
    "type": "FINANCE_CREATED",
    "id": 123,
    "timestamp": "2026-01-01T10:00:00.158Z",
    "userId": 3,
    "payload": {
        "amount": 100.50,
        "description": "Salary"
    }
}
```