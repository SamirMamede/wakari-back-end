# API Wakari - Documentação

## Visão Geral

A API Wakari é o backend para um sistema de gerenciamento de restaurantes. Esta documentação descreve os endpoints disponíveis, como utilizá-los e as funcionalidades implementadas até o momento.

## Base URL

```
http://localhost:8080/api/v1
```

## Documentação Interativa (Swagger)

O projeto utiliza o Springdoc OpenAPI (Swagger) para fornecer uma documentação interativa da API:

- **Swagger UI**: [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/api/v1/v3/api-docs](http://localhost:8080/api/v1/v3/api-docs)

Através da interface do Swagger UI, você pode:
- Explorar todos os endpoints disponíveis
- Ver os modelos de dados
- Testar as requisições diretamente no navegador
- Visualizar os códigos de resposta possíveis

## Endpoints Disponíveis

### Verificação de Status

#### Health Check

- **URL**: `/health`
- **Método**: GET
- **Autenticação**: Não necessária
- **Descrição**: Verifica se a API está funcionando corretamente.
- **Exemplo de Resposta**:
  ```json
  {
    "status": "UP",
    "timestamp": "2025-04-04T10:44:56.954",
    "app": "Wakari API",
    "version": "0.0.1"
  }
  ```

### Autenticação

#### Registro de Usuário

- **URL**: `/auth/register`
- **Método**: POST
- **Autenticação**: Não necessária
- **Descrição**: Registra um novo usuário no sistema.
- **Corpo da Requisição**:
  ```json
  {
    "name": "Nome do Usuário",
    "email": "usuario@exemplo.com",
    "password": "senha123",
    "role": "ADMIN"
  }
  ```
- **Valores válidos para `role`**: `ADMIN`, `GARCOM`, `COZINHA`, `CLIENTE`, `ENTREGADOR`
- **Exemplo de Resposta**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "name": "Nome do Usuário",
    "email": "usuario@exemplo.com",
    "role": "ADMIN"
  }
  ```

#### Login

- **URL**: `/auth/login`
- **Método**: POST
- **Autenticação**: Não necessária
- **Descrição**: Autentica um usuário existente.
- **Corpo da Requisição**:
  ```json
  {
    "email": "usuario@exemplo.com",
    "password": "senha123"
  }
  ```
- **Exemplo de Resposta**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "name": "Nome do Usuário",
    "email": "usuario@exemplo.com",
    "role": "ADMIN"
  }
  ```

## Autenticação via Token JWT

Todos os endpoints protegidos exigem autenticação via token JWT. Para utilizar esses endpoints, você deve incluir o token no cabeçalho da requisição da seguinte forma:

```
Authorization: Bearer seu_token_jwt
```

## Modelo de Dados

### Usuário

| Campo       | Tipo     | Descrição                                          |
|-------------|----------|---------------------------------------------------|
| id          | Long     | Identificador único do usuário                     |
| name        | String   | Nome completo do usuário                           |
| email       | String   | E-mail do usuário (único no sistema)               |
| password    | String   | Senha do usuário (armazenada com criptografia)     |
| role        | Enum     | Função do usuário no sistema (ADMIN, GARCOM, etc.) |
| created_at  | Datetime | Data e hora de criação do registro                 |
| updated_at  | Datetime | Data e hora da última atualização do registro      |

## Configuração da Aplicação

A aplicação está configurada com os seguintes componentes:

- **Spring Boot 3.2.3**: Framework principal para desenvolvimento da API
- **Spring Security**: Para autenticação e autorização
- **JWT**: Para geração e validação de tokens
- **PostgreSQL**: Banco de dados relacional
- **Hibernate/JPA**: Mapeamento objeto-relacional
- **Flyway**: Para migrações de banco de dados
- **BCrypt**: Para criptografia de senhas
- **Springdoc OpenAPI**: Para documentação da API (Swagger)

## Melhorias Implementadas

### Sistema de Autenticação Resiliente
- Tratamento adequado de exceções na validação de tokens JWT
- Logs detalhados para facilitar depuração
- Segurança aprimorada no armazenamento e validação de senhas

### Documentação Interativa (Swagger)
- Interface gráfica para explorar e testar a API
- Modelos detalhados com exemplos
- Anotações para melhorar a legibilidade da documentação

## Instruções para Desenvolvedores

### Configuração do Banco de Dados

O banco de dados PostgreSQL está configurado para rodar na porta 5433. Verifique as configurações no arquivo `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/wakari_db
    username: wakari
    password: senha123
```

### Execução da Aplicação

1. Certifique-se de que o PostgreSQL está em execução na porta 5433:
   ```bash
   cd wakari-back-end
   docker-compose up -d
   ```

2. Execute a aplicação com o comando:
   ```bash
   ./gradlew bootRun
   ```
   
   Ou no Windows:
   ```bash
   gradlew.bat bootRun
   ```

3. A API estará disponível em: `http://localhost:8080/api/v1`
4. A documentação Swagger estará disponível em: `http://localhost:8080/api/v1/swagger-ui/index.html`

### Testes com Postman ou Swagger

#### Usando Postman:
1. Utilize o Postman ou ferramenta similar para testar os endpoints
2. Configure o header `Content-Type: application/json` para requisições que possuem corpo
3. Para endpoints protegidos, adicione o header `Authorization: Bearer seu_token_jwt`

#### Usando Swagger UI:
1. Acesse `http://localhost:8080/api/v1/swagger-ui/index.html`
2. Teste o endpoint `/auth/login` para obter um token
3. Clique no botão "Authorize" e insira o token obtido
4. Explore e teste os demais endpoints

## Próximas Funcionalidades

As seguintes funcionalidades estão planejadas para implementação:

- Gerenciamento de mesas
- Sistema de pedidos
- Controle de estoque
- Módulo de entrega
- Relatórios

## Resolução de Problemas Comuns

- **Erro 401 Unauthorized**: Verifique se o token JWT é válido e está sendo enviado corretamente no header
- **Erro 403 Forbidden**: Verifique se o usuário tem as permissões necessárias para acessar o recurso
- **Erro de conexão com o banco de dados**: Confirme que o PostgreSQL está rodando na porta correta (5433)
- **Swagger não carrega**: Verifique se a aplicação está rodando e se o caminho está correto (/api/v1/swagger-ui/index.html)

## Histórico de Atualizações

### Versão 0.0.1 (Abril/2025)

- Implementação inicial do sistema
- Configuração de banco de dados PostgreSQL
- Sistema de autenticação com JWT
- Registro e login de usuários
- Endpoint de verificação de saúde da API
- Documentação interativa com Swagger 