---
mode: agent
description: Faz o build da aplicação, exporta o spec OpenAPI e gera o docs/openapi/spec.html via Redoc.
---

Gere a documentação atualizada da finance-api executando os passos abaixo em sequência.

## Passos

### 1. Build sem testes

```bash
mvn clean package -DskipTests
```

Aguarde a conclusão. Se falhar, reporte o erro e interrompa.

### 2. Exportar spec OpenAPI

Com a aplicação empacotada, verifique se `docs/openapi/spec.json` já existe e está atualizado.

Se o projeto possuir um endpoint `/v3/api-docs`, exporte o JSON atualizado para `docs/openapi/spec.json` (ex: via curl ou executando a aplicação temporariamente).

### 3. Gerar spec.html

Execute o script PowerShell de geração:

```powershell
.\scripts\generate-docs.ps1
```

Confirme que `docs/openapi/spec.html` foi gerado ou atualizado com sucesso.

### 4. Validar resultado

- Confirmar que `docs/openapi/spec.json` e `docs/openapi/spec.html` existem e não estão vazios
- Reportar o caminho final dos arquivos gerados

## Referências

- [Swagger](../../docs/swagger.md) — padrões de documentação OpenAPI do projeto
- Script de geração: `scripts/generate-docs.ps1`
