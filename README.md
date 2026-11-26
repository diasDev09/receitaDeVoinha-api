# Receita de Voinha — API (AV2)

API REST em **Java 25 + Spring Boot** que serve o front-end [Receita de Voinha](https://github.com/diasDev09/receitaDeVoinha) (React + Vite). Projeto da AV2 da disciplina de Front-end Frameworks, Opção B (API do projeto de Back-end): autenticação, leitura, criação e alteração de estado, com os endpoints protegidos respondendo 401/403. Autor: Caio Dias de Miranda (projeto individual, com autorização do professor).

## Como executar

Pré-requisitos: JDK 25 ou superior (`java -version`) e [Maven](https://maven.apache.org/) 3.9 ou superior (`mvn -v`). Não precisa instalar banco de dados.

```bash
git clone https://github.com/diasDev09/receitaDeVoinha-api.git
cd receitaDeVoinha-api
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

### Banco de dados: Supabase (persistente) ou H2 (em memória)

| Situação | Banco | Os dados sobrevivem ao reinício? |
| --- | --- | --- |
| Sem `.env` (padrão) | H2 em memória, modo PostgreSQL | Não: voltam ao estado inicial |
| Com `.env` apontando para o Supabase | PostgreSQL do Supabase | **Sim** |

Para usar o Supabase:

1. Crie um projeto no [Supabase](https://supabase.com/dashboard) e guarde a senha do banco.
2. Copie `.env.example` para `.env` (Windows: `copy .env.example .env`). O `.env` está no `.gitignore`.
3. Em **Connect → Direct → Session pooler**, copie o host e o usuário para `DB_URL` e `DB_USER`, e coloque a senha em `DB_PASSWORD`.
4. Rode `mvn spring-boot:run`. Na primeira vez, o Flyway cria as tabelas no schema `voinha` e insere os dados iniciais; nas próximas, só confere que já está tudo aplicado.

As tabelas ficam no schema `voinha`, e não no `public`, e a Data API do Supabase está desligada no projeto: os dados (inclusive o hash das senhas) só são acessados pela nossa API.

Para usar com o front-end, rode o front em `http://localhost:5173` (origem já liberada no CORS) e aponte `VITE_API_URL` para `http://localhost:8080`.

### Contas de demonstração

| Usuário | Senha | Papel na demonstração |
| --- | --- | --- |
| `voinha` | `voinha123` | Autora das 9 receitas iniciais |
| `neto` | `neto123` | Usuário comum: recebe 403 ao tentar excluir receita da Voinha |

No banco (migração `V2__dados_iniciais.sql`) só existe o **hash BCrypt** dessas senhas.

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

src/main/resources/
├── application.properties
└── db/migration/ # Flyway: V1__esquema.sql (tabelas) e V2__dados_iniciais.sql
```

## Configuração

Variáveis de ambiente ou arquivo `.env` (modelo em `.env.example`):

| Variável / propriedade | Padrão | Para que serve |
| --- | --- | --- |
| `DB_URL` | H2 em memória | JDBC do banco. Supabase: `jdbc:postgresql://aws-1-[regiao].pooler.supabase.com:5432/postgres?sslmode=require` |
| `DB_USER` | `sa` | Usuário do banco (Supabase: `postgres.[ref-do-projeto]`) |
| `DB_PASSWORD` | vazio | Senha do banco (só no `.env`, nunca no git) |
| `DB_POOL_SIZE` | `5` | Conexões simultâneas (o plano gratuito do Supabase tem limite) |
| `CORS_ORIGINS` | `http://localhost:5173` | Origens do front liberadas no CORS (separe por vírgula) |
| `app.sessao.horas` | `8` | Validade do token de sessão |

Nenhuma senha em texto, token ou segredo fica no repositório.

## Banco e migrações

- As tabelas são criadas pelo **Flyway** a partir de `src/main/resources/db/migration`. Cada arquivo roda uma única vez por banco; o histórico fica na tabela `flyway_schema_history`.
- `V1__esquema.sql`: tabelas `categorias`, `usuarios`, `receitas`, `receita_ingredientes`, `receita_preparo` e `usuario_favoritos`.
- `V2__dados_iniciais.sql`: gerado a partir de `src/data/recipes.js` e `categories.js` da AV1 (ids 1 a 9, mesma ordem de ingredientes e passos), mais os dois usuários de demonstração. Todas as receitas iniciais são da Voinha.
- O Hibernate não cria tabelas (`ddl-auto=validate`): ele só confere, ao subir, se as entidades batem com o banco.
- Para mudar o banco, crie um novo arquivo (`V3__...sql`); nunca edite uma migração que já rodou no Supabase.

## Limitações

- Sem `.env`, a API usa o H2 em memória: receitas cadastradas e favoritos não persistem entre execuções. Com o Supabase, persistem.
- Sessões (tokens) ficam na memória da API: ao reiniciar, todos precisam entrar de novo, mesmo com o Supabase.
- O plano gratuito do Supabase pausa o projeto depois de uma semana sem uso; é só reativá-lo no painel.
- Sem cadastro de usuários e sem edição de receita.
- Sessões em memória: não servem para mais de uma instância da API.
- Sem testes automatizados; a verificação foi feita com `curl` (login, 400/401/403/404/405, cadastro, favoritos, exclusão e logout) e pelo front.
