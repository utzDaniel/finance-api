# Instruções do Copilot - API de finanças

## Regra de Idioma (OBRIGATÓRIA)

- Todas as respostas DEVEM ser escritas em português (pt-BR)
- Os termos técnicos podem permanecer em inglês quando apropriado

## Contexto (OBRIGATÓRIO)

O Copilot DEVE ler a documentação do projeto:

- README.md

---

## Fonte (OBRIGATÓRIO)

O Copilot DEVE seguir a documentação do projeto:

- [Arquitetura](docs/architecture.md)
- [API REST](docs/api.md)
- [Eventos](docs/events.md)
- [Segurança](docs/security.md)
- [Swagger](docs/swagger.md)
- [Testes](docs/testing.md)
- [Convenções](docs/conventions.md)

NÃO duplique regras. Siga sempre estes documentos.

---

## Regras de Atualização Obrigatórias (CRÍTICAS)

Para cada nova implementação, o Copilot DEVE:

### 1. Atualizar a Documentação da API

- Atualizar a documentação OpenAPI (Swagger)
- Garantir que todos os endpoints incluam:
    - resumo
    - descrição
    - esquema da requisição
    - esquema da resposta
    - exemplos

- Seguir rigorosamente:
    - [Swagger](docs/swagger.md)
    - [API REST](docs/api.md)

- Se um novo endpoint for criado:
    - Ele DEVE ser documentado no OpenAPI
    - Ele DEVE seguir as regras de nomenclatura e versionamento

---

### 2. Atualizar os Eventos de Domínio

- Se a implementação for uma operação de escrita:
    - DEVE publicar um evento
    - DEVE seguir a convenção de nomenclatura:
        - Atualize a documentação do evento, se necessário:
        - [Eventos](docs/events.md)

---

### 3. Criar ou Atualizar Testes

- É OBRIGATÓRIO criar testes unitários para a nova funcionalidade
- É OBRIGATÓRIO seguir:
    - [Testes](docs/testing.md)

- Os testes DEVEM:
    - Cobrir a lógica de negócios
    - Validar o comportamento esperado
    - Validar cenários de erro
    - Validar a publicação de eventos (quando aplicável)

---

### 4. Garantir que os Testes Não Sejam Quebrados

- Após qualquer alteração:
    - Todos os testes existentes DEVEM passar
    - Regressões não são permitidas

- Se uma alteração quebrar os testes:
    - Os testes DEVEM ser corrigidos ou atualizados de acordo

---

### 5. Manter a Consistência

- Seguir as convenções de nomenclatura [API REST](docs/api.md)
- Seguir as regras de segurança [Segurança](docs/security.md)
- Seguir as regras de escopo [Segurança](docs/security.md)

---

### 6. Atualizar o README dos Planos

- Se um plano de execução foi executado:
    - Atualizar o status do plano (ex: `approved` → `completed`)
    - Adicionar ou atualizar a entrada correspondente em `docs/plans/README.md`
    - A entrada DEVE incluir: nome do arquivo, descrição resumida e status atual

---

### 7. Comandos de Build e Testes (OBRIGATÓRIO)

- Sempre usar `mvn clean` antes de qualquer build ou execução de testes para garantir que o cache do Maven não interfira nos resultados
- Comando para build: `mvn clean package -DskipTests`
- Comando para testes: `mvn clean test`
- Nunca usar `mvn package` ou `mvn test` sem o `clean`

---

## Exemplo de Fluxo de Trabalho

Ao criar um novo recurso:

1. Criar Controlador + Serviço + DTO
2. Documentar o endpoint no OpenAPI
3. Adicionar evento de operação (se aplicável)
4. Criar testes unitários
5. Garantir que todos os testes sejam aprovados
6. Garantir que a documentação seja atualizada
7. Atualizar `docs/plans/README.md` com o status do plano executado