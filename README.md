# Receita de Voinha — API (AV2)

API REST em **Java 21 + Spring Boot** que serve as receitas do front-end
[receitaDeVoinha](https://github.com/diasDev09/receitaDeVoinha) (React + Vite).
Projeto da AV2 da disciplina de Front-end Frameworks, Opção B (API do projeto de Back-end).

## Como executar

Pré-requisito: JDK 21 ou superior (`java -version`) e Maven. Não precisa instalar banco de dados.

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. O banco é o **H2 em memória**: ao reiniciar, os dados voltam
ao estado inicial de `src/main/resources/data.sql` (9 receitas e 6 categorias da AV1).

Teste rápido:

```bash
curl http://localhost:8080/api/categorias
curl "http://localhost:8080/api/receitas?busca=queijo"
curl http://localhost:8080/api/receitas/1
```

## Endpoints desta etapa

| Método e caminho | Acesso | Resposta |
| --- | --- | --- |
| `GET /api/receitas?busca=&categoria=` | Público | 200 com a lista (busca sem acento e sem caixa em nome, descrição e ingredientes) |
| `GET /api/receitas/{id}` | Público | 200 com a receita; 404 se não existe; 400 se o id não é número |
| `GET /api/categorias` | Público | 200 com a lista |

Login, cadastro de receita e favoritos entram nas próximas fases do roadmap.

### Formato da receita

Os campos têm os mesmos nomes do `recipes.js` da AV1, para o front funcionar sem reescrita:

```json
{
  "id": 1,
  "title": "Feijoada Caseira",
  "category": "Carnes",
  "description": "Uma receita tradicional...",
  "image": "/images/feijoada.png",
  "rating": 4.9,
  "time": "1h30",
  "ingredients": ["500 g de feijão preto", "..."],
  "preparation": ["Deixe o feijão de molho...", "..."]
}
```

As imagens ficam na pasta `public/images` do front; a API devolve só o caminho.

### Formato de erro

```json
{ "status": 404, "message": "Receita 99 não encontrada.", "fields": {} }
```

`fields` leva os erros por campo quando houver validação (próximas fases).

## Estrutura

```
src/main/java/com/receitadevoinha/api/
├── controller/   # rotas HTTP, sem regra de negócio
├── service/      # regras (busca e filtro)
├── repository/   # Spring Data JPA
├── model/        # entidades Categoria e Receita
├── dto/          # JSON de resposta no formato da AV1
├── exception/    # erros e formato único de resposta
└── config/       # CORS
```

## Configuração

| Variável de ambiente | Padrão | Para que serve |
| --- | --- | --- |
| `CORS_ORIGINS` | `http://localhost:5173` | Origens do front liberadas no CORS (separe por vírgula) |

Nenhuma senha ou segredo fica no repositório.

## Dados iniciais

`data.sql` foi gerado a partir de `src/data/recipes.js` e `categories.js` da AV1, mantendo os ids 1 a 9
(os favoritos de teste continuam válidos) e a ordem dos ingredientes e passos.
