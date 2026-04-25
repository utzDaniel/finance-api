# Convenções

## Regras

- Controllers enxutos — lógica de negócio somente no Service
- Repositories apenas persistência
- Injeção de dependência via construtor
- DTOs para entrada/saída; Mapper para conversão; Enum para valores fixos
- Evitar valores nulos; usar nomes descritivos

---

## Nomenclatura

| Classe | Padrão | Exemplo |
|--------|--------|---------|
| Controller | `<Resource>Controller` | `FinancesController` |
| Service | `<Resource>Service` | `FinancesService` |
| Repository | `<Resource>Repository` | `FinancesRepository` |
| Entity | `<Resource>Entity` | `Finance` |
| Mapper | `<Resource>Mapper` | `FinancesMapper` |
| EventPublisher | `<Resource>EventPublisher` | `FinancesEventPublisher` |
| Request | `<Resource><Action>Request` | `FinancesCreateRequest`, `FinancesUpdateRequest` |
| Response | `<Resource><Action>Response` | `FinancesCreatedResponse`, `FinancesResponse` |
