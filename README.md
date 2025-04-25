# Wakari - Sistema de Gerenciamento de Restaurantes

## Sobre o Projeto

Wakari é um sistema completo para gerenciamento de restaurantes, incluindo controle de mesas, pedidos, estoque, funcionários e entregas. Este repositório contém o backend da aplicação, desenvolvido com Spring Boot em Kotlin.

## Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **Framework**: Spring Boot 3.2.3
- **Persistência**: Spring Data JPA
- **Banco de Dados**: PostgreSQL
- **Migrações**: Flyway
- **Segurança**: Spring Security
- **Autenticação**: JWT (JSON Web Token)
- **Documentação**: Springdoc OpenAPI (Swagger)
- **Build**: Gradle

## Pré-requisitos

- JDK 21
- Docker e Docker Compose
- Gradle

## Configuração do Ambiente

### Banco de Dados (PostgreSQL)

O projeto utiliza PostgreSQL como banco de dados. Para facilitar o desenvolvimento, fornecemos um arquivo `docker-compose.yml` que inicializa o PostgreSQL, antes de executá-lo, certifique-se de estar no diretório correto, na raíz do projeto e com o Docker em execução no seu computador.

Para iniciar o banco de dados:

```bash
cd wakari-back-end
docker-compose up -d
```

## Executando o Projeto

### Utilizando Gradle

```bash
./gradlew bootRun
```

Ou no Windows:

```bash
gradlew.bat bootRun
```

### Compilando e Executando o JAR

```bash
./gradlew build
java -jar build/libs/wakari-0.0.1-SNAPSHOT.jar
```

## Estrutura do Projeto

```
src/main/kotlin/com/samirmamede/wakari/
├── config/            # Configurações da aplicação
├── controller/        # Controladores REST
├── dto/               # Objetos de transferência de dados
├── exception/         # Exceções personalizadas
├── model/             # Entidades do domínio
├── repository/        # Repositórios de dados
├── security/          # Configurações e classes de segurança
├── service/           # Serviços de negócio
└── WakariApplication.kt  # Classe principal
```

## Documentação da API

A documentação completa da API está disponível em:

1. **Arquivo** `API_DOCUMENTATION.md` neste repositório
2. **Swagger UI**: http://localhost:8080/api/v1/swagger-ui/index.html
3. **OpenAPI JSON**: http://localhost:8080/api/v1/v3/api-docs

A documentação Swagger permite testar os endpoints diretamente pelo navegador, com uma interface interativa.

## Endpoints Principais

### Autenticação
- **POST /auth/register** - Registro de novos usuários
- **POST /auth/login** - Login e obtenção de token JWT

### Diagnóstico
- **GET /health** - Verificação do status da API

### Estoque
- **GET /stock** - Listar todos os itens do estoque
- **GET /stock/{id}** - Buscar item do estoque por ID
- **GET /stock/search** - Buscar itens de estoque por nome
- **GET /stock/low-stock** - Listar itens com estoque baixo
- **POST /stock** - Criar novo item de estoque
- **PUT /stock/{id}** - Atualizar item de estoque
- **PATCH /stock/{id}/quantity** - Atualizar quantidade de um item no estoque
- **POST /stock/{id}/add** - Adicionar quantidade a um item do estoque
- **POST /stock/{id}/remove** - Remover quantidade de um item do estoque
- **DELETE /stock/{id}** - Excluir item do estoque

### Pedidos
- **GET /orders** - Listar todos os pedidos
- **GET /orders/{id}** - Buscar pedido por ID
- **GET /orders/status/{status}** - Buscar pedidos por status
- **GET /orders/user/{userId}** - Buscar pedidos de um usuário
- **GET /orders/date-range** - Buscar pedidos por período
- **POST /orders** - Criar novo pedido
- **POST /orders/{id}/items** - Adicionar itens a um pedido existente
- **PATCH /orders/{id}/status** - Atualizar status do pedido
- **POST /orders/{id}/cancel** - Cancelar pedido

### Cardápio
- **GET /menu** - Listar todos os itens do cardápio
- **GET /menu/{id}** - Buscar item do cardápio por ID
- **GET /menu/available** - Listar itens disponíveis no cardápio
- **GET /menu/category/{category}** - Listar itens do cardápio por categoria
- **GET /menu/categories** - Listar todas as categorias do cardápio
- **POST /menu** - Criar novo item do cardápio
- **PUT /menu/{id}** - Atualizar item do cardápio
- **POST /menu/{id}/recipes** - Adicionar ingrediente a um item do cardápio
- **PUT /menu/{menuItemId}/recipes/{recipeId}** - Atualizar ingrediente na receita
- **DELETE /menu/{menuItemId}/recipes/{recipeId}** - Remover ingrediente da receita
- **POST /menu/update-availability** - Atualizar disponibilidade de todos os itens
- **DELETE /menu/{id}** - Excluir item do cardápio

### Receitas
- **GET /recipes** - Listar todas as receitas
- **GET /recipes/{id}** - Buscar receita por ID
- **GET /recipes/menu-item/{menuItemId}** - Listar receitas por item do cardápio
- **GET /recipes/stock-item/{stockItemId}** - Listar receitas por item de estoque
- **POST /recipes** - Criar nova receita
- **PUT /recipes/{id}** - Atualizar receita
- **DELETE /recipes/{id}** - Excluir receita

### Mesas
- **GET /tables** - Listar todas as mesas
- **GET /tables/{id}** - Buscar mesa por ID
- **GET /tables/number/{number}** - Buscar mesa por número
- **GET /tables/status/{status}** - Listar mesas por status
- **POST /tables** - Criar nova mesa
- **PUT /tables/{id}** - Atualizar mesa existente
- **PATCH /tables/{id}/status** - Atualizar status da mesa
- **POST /tables/cleanup** - Limpar todas as mesas em estado de limpeza
- **DELETE /tables/{id}** - Excluir mesa

## Executando Testes

```bash
./gradlew test
```

## Migrações de Banco de Dados

O projeto utiliza Flyway para gerenciar migrações de banco de dados. Os scripts de migração estão localizados em `src/main/resources/db/migration/`.

Para criar uma nova migração, adicione um arquivo SQL seguindo a convenção de nomenclatura:

```
V{número_sequencial}__{descrição}.sql
```

Exemplo: `V2__add_order_table.sql`

## Status do Projeto

O projeto atualmente implementa:
- Sistema de autenticação e autorização com JWT
- Gerenciamento de estoque com controle de quantidades mínimas
- Sistema de cardápio com categorias e disponibilidade dinâmica
- Gerenciamento de receitas (relação entre itens do cardápio e estoque)
- Atualização automática da disponibilidade dos itens com base no estoque
- Sistema de mesas com controle de status (disponível, ocupada, reservada, em limpeza)
- Sistema de pedidos com vinculação às mesas e gerenciamento de status
- Cálculo automático de valores dos pedidos
- Atualização de estoque automática ao concluir pedidos

Próximos passos:
- Implementação de sistema de pagamentos
- Painel administrativo com visão em tempo real
- Integração com entregadores para delivery

## Contribuindo

1. Clone o repositório
2. Crie uma branch para sua funcionalidade (`git checkout -b feature/nova-funcionalidade`)
3. Faça commit das alterações (`git commit -m 'Adiciona nova funcionalidade'`)
4. Envie para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## Contato

Samir Mamede - [anuarsamir@gmail.com](mailto:anuarsamir@gmail.com)

Link do projeto: [https://github.com/SamirMamede/wakari-back-end](https://github.com/SamirMamede/wakari-back-end) 