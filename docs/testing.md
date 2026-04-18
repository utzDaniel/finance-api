# Testes

## Visão Geral

Este documento define a estratégia de testes para a API de finanças.
O objetivo é garantir a qualidade, confiabilidade e consistência do código em todos os módulos.

---

## Estratégia de Testes

O projeto segue uma abordagem de testes em camadas:

- Testes Unitários → Camada de serviço (foco principal)
- Testes de Integração → Controlador + API (opcional, futuro)

---

## Escopo

Os testes DEVEM abranger:

- Lógica de negócios
- Regras de validação
- Publicação de eventos
- Cenários de erro

---

## Ferramentas

- JUnit 5
- Mockito

---

## Estrutura de Testes

Os testes devem seguir a mesma estrutura de módulos do código principal:

`src/test/java/com/finance/modules/<module>`


Example:

`modules/finance/service/FinancesServiceTest.java`


---

## Convenção de Nomenclatura

Pattern:`

`<MethodName>Should<ExpectedBehavior>`

Exemplos:

- createShouldSaveFinance
- createShouldPublishEvent
- getByIdShouldThrowWhenNotFound

---

## Regras de Teste Unitário

### Camada de Serviço (OBRIGATÓRIO)

- Deve ser totalmente testada
- Deve simular todas as dependências
- Não deve acessar o banco de dados real
- Deve validar as regras de negócio

---

### Camada de Controlador

- Opcional
- Deve testar apenas o comportamento HTTP
- Não deve testar a lógica de negócio

---

### Camada de Repositório

- Não é necessário para testes unitários
- Coberto por testes de integração, se necessário

---

## Regras de Simulação

Todas as dependências externas devem ser simuladas:

- Repositório
- Mapeador
- Publicador de Eventos

---

## Teste de Eventos (MUITO IMPORTANTE)

Toda operação de escrita DEVE verificar a publicação do evento.

---

## Asserções

Os testes devem validar:

- Dados retornados
- Comportamento (chamadas de método)
- Exceções

---

## Isolamento de Testes

- Cada teste deve ser independente
- Nenhum estado compartilhado entre os testes
- Use objetos novos

---

## Boas Práticas

- Mantenha os testes simples e legíveis
- Teste um comportamento por teste
- Evite complexidade desnecessária
- Use nomes descritivos

---

## Antipadrões (Evitar)
- Testar múltiplos comportamentos em um único teste
- Usar banco de dados real em testes unitários
- Não verificar a publicação de eventos
- Ignorar cenários de erro
- Escrever testes sem asserções
