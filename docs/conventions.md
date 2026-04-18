# Convenções

## Regras de Arquitetura

- Controllers devem ser enxutos
- A lógica de negócios deve estar nos services
- Repositories devem lidar apenas com persistência

---

## Padrões

- DTO para entrada/saída
- Mapper para conversão
- Enum para valores fixos

---

## Estilo de Código

- Injeção de dependência no construtor
- Evitar valores nulos
- Usar nomes descritivos

---

## API

- Convenções REST
- Códigos de status HTTP adequados

---

## Convenções de nomenclatura

### Controller

Pattern:

- `<Resource>Controller`

Example:

- FinancesController

---

### Service

Pattern:

- `<Resource>Service`

Example:

- FinancesService

---

### Repository

Pattern:

- `<Resource>Repository`

Example:

- FinancesRepository

---

### Request Objects

Pattern:

- `<Resource><Action>Request`

Examples:

- FinancesCreateRequest
- FinancesUpdateRequest

---

### Response DTOs

Pattern:

- `<Resource><Action>Request`

Examples:

- FinancesCreatedDTO
- FinancesUpdatedDTO
- FinancesDTO (generic response)
