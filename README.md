# Teste Tecnico - Microservico CNAE

Projeto Spring Boot criado para avaliacao de candidatos a vagas de Lider Tecnico e Analista.

## Stack

- Java 25
- Spring Boot 4.1.0
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- Maven

## Como executar

```bash
mvn spring-boot:run
```

H2 Console:

```text
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:cnaedb
User: sa
Password:
```

## Endpoints

```http
GET /api/cnaes
GET /api/cnaes/buscar?termo=programas
GET /api/cnaes/codigo?codigo=6201-5/01
GET /api/cadastros-secundarios
GET /api/cadastros-secundarios/validar-cnae?codigoCnae=6201-5/01
POST /api/cadastros-secundarios
```

Comportamento esperado:

- `GET /api/cnaes` deve retornar todas as atividades cadastradas.
- `GET /api/cnaes/buscar?termo={texto}` deve buscar CNAEs que contenham o texto informado em qualquer parte da descricao, ignorando maiusculas e minusculas.
- `GET /api/cnaes/codigo?codigo={codigo}` deve retornar o CNAE do codigo informado.
- Codigos CNAE inexistentes devem retornar uma resposta HTTP adequada para recurso nao encontrado.
- `POST /api/cadastros-secundarios` deve criar um cadastro vinculado a um CNAE existente.
- `GET /api/cadastros-secundarios/validar-cnae?codigoCnae={codigo}` deve validar se o CNAE informado pode ser usado no cadastro.
- Cadastros secundários com CNAE inexistente nao devem ser criados.

Exemplo:

```bash
curl http://localhost:8080/api/cnaes
curl "http://localhost:8080/api/cnaes/buscar?termo=programas"
curl "http://localhost:8080/api/cnaes/codigo?codigo=6201-5/01"
curl -X POST http://localhost:8080/api/cadastros-secundarios \
  -H "Content-Type: application/json" \
  -d '{"nomeFantasia":"Tech Porto","documento":"12345678000199","codigoCnae":"6201-5/01"}'
```

## Desafio para o candidato

Objetivo:

1. Fazer a aplicacao subir corretamente.
2. Validar os endpoints disponiveis.
3. Identificar e corrigir problemas encontrados durante a execucao.
4. Explicar as causas dos problemas e as decisoes tomadas.
5. Adicionar ou ajustar testes, quando fizer sentido.

## Entrega esperada

- Codigo corrigido em um branch ou pull request.
- Breve explicacao tecnica das alteracoes.
- Evidencias de execucao, como comandos usados, respostas dos endpoints ou testes.

## Diagnóstico e correções realizadas

### 1. Busca parcial de CNAE

#### Problema
A consulta original utilizava o wildcard apenas após o termo:

LIKE 'termo%'

Isso permitia localizar somente descrições iniciadas pelo valor pesquisado.

#### Causa raiz
A implementação da query não correspondia ao requisito de busca por
ocorrência em qualquer parte da descrição.

#### Correção
A consulta foi alterada para:

LIKE '%termo%'

mantendo a utilização de lower() para comparação case-insensitive.

---

### 2. Consulta de CNAE inexistente

#### Problema
Quando um código CNAE não era encontrado, a aplicação retornava
o primeiro CNAE existente no banco.

#### Impacto
A API retornava um recurso diferente do solicitado, mascarando
a inexistência do código informado.

#### Correção
O fallback foi removido e substituído por CnaeNotFoundException.

O tratamento HTTP foi centralizado através de @RestControllerAdvice,
retornando HTTP 404 e ProblemDetail.

---

### 3. Cadastro secundário com CNAE inexistente

#### Problema
Ao cadastrar utilizando um CNAE inexistente, a aplicação utilizava
silenciosamente o primeiro CNAE existente.

#### Impacto
O cadastro era persistido associado a um CNAE diferente daquele
enviado pelo cliente.

#### Correção
A operação passou a exigir a existência do CNAE antes da persistência.

A busca foi centralizada no método buscarCnae(), utilizado tanto no
cadastro quanto na validação, evitando duplicação da regra.