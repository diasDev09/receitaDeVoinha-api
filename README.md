# Receita de Voinha — API (AV2)

API REST em **Java 25 + Spring Boot** que serve o front-end [Receita de Voinha](https://github.com/diasDev09/receitaDeVoinha) (React + Vite). Projeto da AV2 da disciplina de Front-end Frameworks, Opção B (API do projeto de Back-end): autenticação, leitura, criação e alteração de estado, com os endpoints protegidos respondendo 401/403. Autor: Caio Dias de Miranda (projeto individual, com autorização do professor).

## Como executar

Pré-requisitos: JDK 25 ou superior (`java -version`) e [Maven](https://maven.apache.org/) 3.9 ou superior (`mvn -v`). Não precisa instalar banco de dados.

```bash
git clone https://github.com/diasDev09/receitaDeVoinha-api.git
cd receitaDeVoinha-api
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. O banco é o **H2 em memória**: ao reiniciar, os dados voltam ao estado inicial de `src/main/resources/data.sql` (9 receitas, 6 categorias e 2 usuários de demonstração).

Para usar com o front-end, rode o front em `http://localhost:5173` (origem já liberada no CORS) e aponte `VITE_API_URL` para `http://localhost:8080`.

### Contas de demonstração

| Usuário | Senha | Papel na demonstração |
| --- | --- | --- |
| `voinha` | `voinha123` | Autora das 9 receitas iniciais |
| `neto` | `neto123` | Usuário comum: recebe 403 ao tentar excluir receita da Voinha |

No `data.sql` só existe o **hash BCrypt** dessas senhas.

### Teste rápido

```bash
curl http://localhost:8080/api/categorias
curl "http://localhost:8080/api/receitas?busca=queijo"

# login (guarde o token da resposta)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"neto","password":"neto123"}'

# endpoint protegido
curl http://localhost:8080/api/favoritos -H "Authorization: Bearer <token>"
```

## Autenticação

- `POST /api/auth/login` confere a senha com BCrypt e devolve um **token opaco** aleatório (256 bits), guardado na memória da API com validade de 8 horas (`app.sessao.horas`).
- Os endpoints protegidos estão marcados com `@RequerLogin`. O `AuthInterceptor` lê `Authorization: Bearer <token>` e, sem sessão válida, responde **401** antes de chegar ao controller.
- Regras de permissão ficam no serviço: excluir receita de outra pessoa responde **403**.
- `POST /api/auth/logout` invalida o token no servidor.
- Por que token opaco e não JWT: não há chave secreta para guardar ou versionar, e o logout invalida o token de verdade. O custo é que as sessões caem quando a API reinicia (o front trata como sessão expirada).

## Contrato da API

| Método e caminho | Acesso | Resposta |
| --- | --- | --- |
| `POST /api/auth/login` | Público | 200 `{ token, user }`; 401 usuário/senha errados; 400 campo vazio |
| `GET /api/auth/me` | Token | 200 `{ id, name, username }`; 401 |
| `POST /api/auth/logout` | Token | 204; 401 |
| `GET /api/receitas?busca=&categoria=` | Público | 200 com a lista (busca sem acento e sem diferença de maiúsculas em nome, descrição e ingredientes) |
| `GET /api/receitas/{id}` | Público | 200; 404 se não existe; 400 se o `id` não é número |
| `POST /api/receitas` | Token | 201 com a receita criada (autor = usuário logado); 400 com `fields`; 401 |
| `DELETE /api/receitas/{id}` | Token | 204; **403** se não for o autor; 404; 401 |
| `GET /api/categorias` | Público | 200 com a lista |
| `GET /api/favoritos` | Token | 200 com as receitas favoritas do usuário |
| `PUT /api/favoritos/{receitaId}` | Token | 204 (idempotente); 404 se a receita não existe; 401 |
| `DELETE /api/favoritos/{receitaId}` | Token | 204 (idempotente); 401 |

Método não suportado responde 405, rota inexistente 404 e falhas inesperadas 500, sempre no formato de erro abaixo e sem expor detalhes internos.

### Login

```json
// POST /api/auth/login
{ "username": "neto", "password": "neto123" }

// 200
{ "token": "tvmjdUHyKwBe...", "user": { "id": 2, "name": "Neto Curioso", "username": "neto" } }
```

### Receita

Os campos têm os mesmos nomes do `recipes.js` da AV1, para o front funcionar sem reescrita. `authorId` e `author` foram acrescentados na AV2.

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
  "preparation": ["Deixe o feijão de molho...", "..."],
  "authorId": 1,
  "author": "Dona Voinha"
}
```

Corpo de `POST /api/receitas` (mesmas regras de `validateRecipe` do front; `image` é opcional):

```json
{
  "title": "Bolo de fubá",
  "category": "Sobremesas",
  "time": "40min",
  "description": "Um bolo bem fofinho de vó",
  "image": "",
  "ingredients": ["2 ovos", "1 xícara de fubá"],
  "preparation": ["Misture tudo", "Asse por 40 minutos"]
}
```

As imagens ficam na pasta `public/images` do front; a API devolve só o caminho.

### Formato de erro

```json
{ "status": 400, "message": "Verifique os campos destacados.", "fields": { "title": "O nome deve ter entre 3 e 100 caracteres." } }
{ "status": 401, "message": "Sessão ausente ou expirada. Faça login novamente.", "fields": {} }
{ "status": 403, "message": "Você só pode excluir receitas que você cadastrou.", "fields": {} }
```

`fields` usa os mesmos nomes dos campos do formulário do front, que marca cada erro no campo certo.

## Estrutura

```
src/main/java/com/receitadevoinha/api/
├── auth/         # @RequerLogin, AuthInterceptor (401) e SessaoService (tokens)
├── controller/   # rotas HTTP, sem regra de negócio
├── service/      # regras: busca, cadastro, exclusão (403), favoritos, login
├── repository/   # Spring Data JPA
├── model/        # entidades Categoria, Receita e Usuario
├── dto/          # JSON de entrada (com validação) e de saída
├── exception/    # erros e formato único de resposta
└── config/       # CORS, interceptor e BCrypt
```

## Configuração

| Variável / propriedade | Padrão | Para que serve |
| --- | --- | --- |
| `CORS_ORIGINS` | `http://localhost:5173` | Origens do front liberadas no CORS (separe por vírgula) |
| `app.sessao.horas` | `8` | Validade do token de sessão |

Nenhuma senha em texto, token ou segredo fica no repositório.

## Dados iniciais

`data.sql` foi gerado a partir de `src/data/recipes.js` e `categories.js` da AV1, mantendo os ids 1 a 9 e a ordem dos ingredientes e passos. Na AV2 ganhou os dois usuários de demonstração; todas as receitas iniciais são da Voinha.

## Limitações

- Banco H2 em memória: receitas cadastradas, favoritos e sessões não persistem entre execuções.
- Sem cadastro de usuários e sem edição de receita.
- Sessões em memória: não servem para mais de uma instância da API.
- Sem testes automatizados; a verificação foi feita com `curl` (login, 400/401/403/404/405, cadastro, favoritos, exclusão e logout) e pelo front.
