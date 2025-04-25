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

### Mesas

#### Listar Todas as Mesas

- **URL**: `/tables?page=0&size=10&sort=number`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER
- **Descrição**: Retorna todas as mesas do restaurante, com paginação.
- **Parâmetros de Consulta**:
  - `page`: Número da página (padrão: 0)
  - `size`: Tamanho da página (padrão: 10)
  - `sort`: Campo para ordenação (padrão: number)
- **Exemplo de Resposta**:
  ```json
  {
    "content": [
      {
        "id": 1,
        "number": 1,
        "capacity": 4,
        "status": "AVAILABLE",
        "currentOrderId": null,
        "createdAt": "2025-04-04T10:44:56.954",
        "updatedAt": "2025-04-04T10:44:56.954"
      },
      {
        "id": 2,
        "number": 2,
        "capacity": 6,
        "status": "OCCUPIED",
        "currentOrderId": 1,
        "createdAt": "2025-04-04T10:44:56.954",
        "updatedAt": "2025-04-04T10:44:56.954"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 2,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 2,
    "first": true,
    "empty": false
  }
  ```

#### Buscar Mesa por ID

- **URL**: `/tables/{id}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER
- **Descrição**: Retorna uma mesa específica pelo seu ID.
- **Exemplo de Resposta**:
  ```json
  {
    "id": 1,
    "number": 1,
    "capacity": 4,
    "status": "AVAILABLE",
    "currentOrderId": null,
    "createdAt": "2025-04-04T10:44:56.954",
    "updatedAt": "2025-04-04T10:44:56.954"
  }
  ```

#### Buscar Mesa por Número

- **URL**: `/tables/number/{number}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER
- **Descrição**: Busca uma mesa pelo seu número.
- **Exemplo de Resposta**: Similar ao endpoint de busca por ID.

#### Listar Mesas por Status

- **URL**: `/tables/status/{status}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER
- **Descrição**: Lista todas as mesas com um status específico (AVAILABLE, OCCUPIED, RESERVED, CLEANING).
- **Exemplo de Resposta**:
  ```json
  [
    {
      "id": 1,
      "number": 1,
      "capacity": 4,
      "status": "AVAILABLE",
      "currentOrderId": null,
      "createdAt": "2025-04-04T10:44:56.954",
      "updatedAt": "2025-04-04T10:44:56.954"
    },
    {
      "id": 3,
      "number": 3,
      "capacity": 2,
      "status": "AVAILABLE",
      "currentOrderId": null,
      "createdAt": "2025-04-04T10:44:56.954",
      "updatedAt": "2025-04-04T10:44:56.954"
    }
  ]
  ```

#### Criar Nova Mesa

- **URL**: `/tables`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Corpo da Requisição**:
  ```json
  {
    "number": 5,
    "capacity": 8
  }
  ```
- **Exemplo de Resposta**: A mesa criada, similar ao endpoint de busca por ID.

#### Atualizar Mesa Existente

- **URL**: `/tables/{id}`
- **Método**: PUT
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Corpo da Requisição**:
  ```json
  {
    "number": 5,
    "capacity": 10,
    "status": "RESERVED"
  }
  ```
- **Exemplo de Resposta**: A mesa atualizada.

#### Atualizar Status da Mesa

- **URL**: `/tables/{id}/status`
- **Método**: PATCH
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER
- **Corpo da Requisição**:
  ```json
  {
    "status": "OCCUPIED"
  }
  ```
- **Valores válidos para `status`**: `AVAILABLE`, `OCCUPIED`, `RESERVED`, `CLEANING`
- **Exemplo de Resposta**: A mesa com o status atualizado.

#### Limpar Todas as Mesas em Limpeza

- **URL**: `/tables/cleanup`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER
- **Descrição**: Altera o status de todas as mesas em limpeza (CLEANING) para disponível (AVAILABLE).
- **Resposta**: 200 OK

#### Excluir Mesa

- **URL**: `/tables/{id}`
- **Método**: DELETE
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Descrição**: Exclui uma mesa do sistema (apenas se não estiver associada a nenhum pedido).
- **Resposta**: 204 No Content

### Pedidos

#### Listar Todos os Pedidos

- **URL**: `/orders?page=0&size=10`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER, KITCHEN
- **Descrição**: Retorna todos os pedidos, com paginação.
- **Parâmetros de Consulta**:
  - `page`: Número da página (padrão: 0)
  - `size`: Tamanho da página (padrão: 10)
- **Exemplo de Resposta**:
  ```json
  {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "userName": "João Silva",
        "tableId": 2,
        "tableNumber": 2,
        "total": 65.80,
        "status": "PENDING",
        "isDelivery": false,
        "items": [
          {
            "id": 1,
            "menuItemId": 3,
            "menuItemName": "Pizza Margherita",
            "quantity": 1,
            "priceAtTime": 45.90,
            "subtotal": 45.90,
            "notes": "Sem cebola",
            "createdAt": "2025-04-04T10:44:56.954",
            "updatedAt": "2025-04-04T10:44:56.954"
          },
          {
            "id": 2,
            "menuItemId": 8,
            "menuItemName": "Refrigerante Cola",
            "quantity": 2,
            "priceAtTime": 9.95,
            "subtotal": 19.90,
            "notes": null,
            "createdAt": "2025-04-04T10:44:56.954",
            "updatedAt": "2025-04-04T10:44:56.954"
          }
        ],
        "createdAt": "2025-04-04T10:44:56.954",
        "updatedAt": "2025-04-04T10:44:56.954"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 1,
    "first": true,
    "empty": false
  }
  ```

#### Buscar Pedido por ID

- **URL**: `/orders/{id}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER, KITCHEN
- **Descrição**: Retorna um pedido específico pelo seu ID.
- **Exemplo de Resposta**:
  ```json
  {
    "id": 1,
    "userId": 1,
    "userName": "João Silva",
    "tableId": 2,
    "tableNumber": 2,
    "total": 65.80,
    "status": "PENDING",
    "isDelivery": false,
    "items": [
      {
        "id": 1,
        "menuItemId": 3,
        "menuItemName": "Pizza Margherita",
        "quantity": 1,
        "priceAtTime": 45.90,
        "subtotal": 45.90,
        "notes": "Sem cebola",
        "createdAt": "2025-04-04T10:44:56.954",
        "updatedAt": "2025-04-04T10:44:56.954"
      },
      {
        "id": 2,
        "menuItemId": 8,
        "menuItemName": "Refrigerante Cola",
        "quantity": 2,
        "priceAtTime": 9.95,
        "subtotal": 19.90,
        "notes": null,
        "createdAt": "2025-04-04T10:44:56.954",
        "updatedAt": "2025-04-04T10:44:56.954"
      }
    ],
    "createdAt": "2025-04-04T10:44:56.954",
    "updatedAt": "2025-04-04T10:44:56.954"
  }
  ```

#### Buscar Pedidos por Status

- **URL**: `/orders/status/{status}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER, KITCHEN
- **Descrição**: Retorna todos os pedidos com um status específico.
- **Valores válidos para `status`**: `PENDING`, `PREPARING`, `READY`, `DELIVERED`, `CANCELED`
- **Exemplo de Resposta**: Similar ao endpoint de listagem.

#### Buscar Pedidos de um Usuário

- **URL**: `/orders/user/{userId}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER, ou o próprio usuário
- **Descrição**: Retorna todos os pedidos de um usuário específico.
- **Exemplo de Resposta**: Similar ao endpoint de listagem.

#### Buscar Pedidos por Período

- **URL**: `/orders/date-range?startDate=2025-04-01&endDate=2025-04-05`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Descrição**: Retorna todos os pedidos criados dentro de um intervalo de datas.
- **Parâmetros de Consulta**:
  - `startDate`: Data inicial (formato ISO: YYYY-MM-DD)
  - `endDate`: Data final (formato ISO: YYYY-MM-DD)
- **Exemplo de Resposta**: Similar ao endpoint de listagem.

#### Criar Novo Pedido

- **URL**: `/orders`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER, CUSTOMER
- **Corpo da Requisição**:
  ```json
  {
    "userId": 1,
    "tableId": 2,
    "isDelivery": false,
    "items": [
      {
        "menuItemId": 3,
        "quantity": 1,
        "notes": "Sem cebola"
      },
      {
        "menuItemId": 8,
        "quantity": 2,
        "notes": null
      }
    ]
  }
  ```
- **Exemplo de Resposta**: O pedido criado, similar ao endpoint de busca por ID.

#### Adicionar Itens a um Pedido

- **URL**: `/orders/{id}/items`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER
- **Descrição**: Adiciona novos itens a um pedido existente.
- **Corpo da Requisição**:
  ```json
  {
    "items": [
      {
        "menuItemId": 5,
        "quantity": 1,
        "notes": "Sem gelo"
      }
    ]
  }
  ```
- **Exemplo de Resposta**: O pedido atualizado.

#### Atualizar Status do Pedido

- **URL**: `/orders/{id}/status`
- **Método**: PATCH
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER, KITCHEN
- **Descrição**: Atualiza o status de um pedido.
- **Corpo da Requisição**:
  ```json
  {
    "status": "PREPARING"
  }
  ```
- **Valores válidos para `status`**: `PENDING`, `PREPARING`, `READY`, `DELIVERED`, `CANCELED`
- **Exemplo de Resposta**: O pedido com o status atualizado.

#### Cancelar Pedido

- **URL**: `/orders/{id}/cancel`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, WAITER, ou o cliente que fez o pedido
- **Descrição**: Cancela um pedido existente, alterando seu status para CANCELED.
- **Exemplo de Resposta**: O pedido cancelado.

### Estoque

#### Listar Todos os Itens de Estoque

- **URL**: `/stock`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Descrição**: Retorna todos os itens do estoque.
- **Exemplo de Resposta**:
  ```json
  [
    {
      "id": 1,
      "name": "Tomate",
      "quantity": 10.50,
      "unit": "kg",
      "minQuantity": 2.00,
      "isLowStock": false,
      "createdAt": "2025-04-04T10:44:56.954",
      "updatedAt": "2025-04-04T10:44:56.954"
    },
    {
      "id": 2,
      "name": "Cebola",
      "quantity": 5.00,
      "unit": "kg",
      "minQuantity": 1.00,
      "isLowStock": false,
      "createdAt": "2025-04-04T10:44:56.954",
      "updatedAt": "2025-04-04T10:44:56.954"
    }
  ]
  ```

#### Buscar Item de Estoque por ID

- **URL**: `/stock/{id}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Descrição**: Retorna um item específico do estoque pelo seu ID.
- **Exemplo de Resposta**:
  ```json
  {
    "id": 1,
    "name": "Tomate",
    "quantity": 10.50,
    "unit": "kg",
    "minQuantity": 2.00,
    "isLowStock": false,
    "createdAt": "2025-04-04T10:44:56.954",
    "updatedAt": "2025-04-04T10:44:56.954"
  }
  ```

#### Buscar Itens de Estoque por Nome

- **URL**: `/stock/search?name=valor`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Descrição**: Busca itens do estoque pelo nome (busca parcial, case insensitive).
- **Exemplo de Resposta**: Similar ao endpoint de listagem.

#### Listar Itens com Estoque Baixo

- **URL**: `/stock/low-stock`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Descrição**: Lista todos os itens do estoque cuja quantidade está abaixo ou igual à quantidade mínima definida.
- **Exemplo de Resposta**: Similar ao endpoint de listagem.

#### Criar Novo Item de Estoque

- **URL**: `/stock`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Corpo da Requisição**:
  ```json
  {
    "name": "Tomate",
    "quantity": 10.50,
    "unit": "kg",
    "minQuantity": 2.00
  }
  ```
- **Exemplo de Resposta**: O item criado, similar ao endpoint de busca por ID.

#### Atualizar Item de Estoque

- **URL**: `/stock/{id}`
- **Método**: PUT
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Corpo da Requisição**: Similar ao da criação.
- **Exemplo de Resposta**: O item atualizado.

#### Atualizar Quantidade de um Item no Estoque

- **URL**: `/stock/{id}/quantity`
- **Método**: PATCH
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Corpo da Requisição**:
  ```json
  {
    "quantity": 15.75
  }
  ```
- **Exemplo de Resposta**: O item atualizado.

#### Adicionar Quantidade a um Item do Estoque

- **URL**: `/stock/{id}/add`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Corpo da Requisição**:
  ```json
  {
    "quantity": 5.00
  }
  ```
- **Exemplo de Resposta**: O item atualizado.

#### Remover Quantidade de um Item do Estoque

- **URL**: `/stock/{id}/remove`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Corpo da Requisição**:
  ```json
  {
    "quantity": 3.00
  }
  ```
- **Exemplo de Resposta**: O item atualizado.

#### Excluir Item do Estoque

- **URL**: `/stock/{id}`
- **Método**: DELETE
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Descrição**: Exclui um item do estoque (apenas se não estiver associado a nenhuma receita).
- **Resposta**: 204 No Content

### Cardápio

#### Listar Todos os Itens do Cardápio

- **URL**: `/menu?page=0&size=10&sort=name`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Descrição**: Retorna todos os itens do cardápio, com paginação.
- **Parâmetros de Consulta**:
  - `page`: Número da página (padrão: 0)
  - `size`: Tamanho da página (padrão: 10)
  - `sort`: Campo para ordenação (padrão: name)
- **Exemplo de Resposta**:
  ```json
  {
    "content": [
      {
        "id": 1,
        "name": "Pizza Margherita",
        "description": "Pizza com molho de tomate, mussarela e manjericão",
        "price": 45.90,
        "available": true,
        "imageUrl": "https://exemplo.com/margherita.jpg",
        "category": "Pizzas",
        "recipes": [
          {
            "id": 1,
            "stockItemId": 1,
            "stockItemName": "Tomate",
            "quantity": 0.2,
            "unit": "kg"
          },
          {
            "id": 2,
            "stockItemId": 3,
            "stockItemName": "Mussarela",
            "quantity": 0.25,
            "unit": "kg"
          }
        ],
        "createdAt": "2025-04-04T10:44:56.954",
        "updatedAt": "2025-04-04T10:44:56.954"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 1,
    "first": true,
    "empty": false
  }
  ```

#### Buscar Item do Cardápio por ID

- **URL**: `/menu/{id}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Descrição**: Retorna um item específico do cardápio pelo seu ID.
- **Exemplo de Resposta**: Similar ao conteúdo do endpoint de listagem.

#### Listar Itens Disponíveis no Cardápio

- **URL**: `/menu/available?page=0&size=10&sort=name`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Descrição**: Lista apenas os itens disponíveis no cardápio.
- **Exemplo de Resposta**: Similar ao endpoint de listagem.

#### Listar Itens do Cardápio por Categoria

- **URL**: `/menu/category/{category}?page=0&size=10&sort=name`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Descrição**: Lista os itens do cardápio de uma categoria específica.
- **Exemplo de Resposta**: Similar ao endpoint de listagem.

#### Listar Todas as Categorias do Cardápio

- **URL**: `/menu/categories`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Descrição**: Retorna todas as categorias distintas existentes no cardápio.
- **Exemplo de Resposta**:
  ```json
  ["Pizzas", "Hambúrgueres", "Bebidas", "Sobremesas"]
  ```

#### Criar Novo Item do Cardápio

- **URL**: `/menu`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Corpo da Requisição**:
  ```json
  {
    "name": "Pizza Margherita",
    "description": "Pizza com molho de tomate, mussarela e manjericão",
    "price": 45.90,
    "imageUrl": "https://exemplo.com/margherita.jpg",
    "category": "Pizzas"
  }
  ```
- **Exemplo de Resposta**: O item criado, similar ao endpoint de busca por ID.

#### Atualizar Item do Cardápio

- **URL**: `/menu/{id}`
- **Método**: PUT
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Corpo da Requisição**:
  ```json
  {
    "name": "Pizza Margherita",
    "description": "Pizza com molho de tomate, mussarela e manjericão fresco",
    "price": 49.90,
    "available": true,
    "imageUrl": "https://exemplo.com/margherita.jpg",
    "category": "Pizzas"
  }
  ```
- **Exemplo de Resposta**: O item atualizado.

#### Adicionar Ingrediente (Receita) a um Item do Cardápio

- **URL**: `/menu/{id}/recipes`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Corpo da Requisição**:
  ```json
  {
    "menuItemId": 1,
    "stockItemId": 1,
    "quantity": 0.2
  }
  ```
- **Exemplo de Resposta**: O item do cardápio com a receita adicionada.

#### Atualizar Quantidade de um Ingrediente na Receita

- **URL**: `/menu/{menuItemId}/recipes/{recipeId}`
- **Método**: PUT
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Corpo da Requisição**:
  ```json
  {
    "quantity": 0.25
  }
  ```
- **Exemplo de Resposta**: A receita atualizada.

#### Remover Ingrediente da Receita de um Item do Cardápio

- **URL**: `/menu/{menuItemId}/recipes/{recipeId}`
- **Método**: DELETE
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Descrição**: Remove um ingrediente da receita de um item do cardápio.
- **Resposta**: 204 No Content

#### Atualizar a Disponibilidade de Todos os Itens do Cardápio

- **URL**: `/menu/update-availability`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Descrição**: Atualiza a disponibilidade de todos os itens do cardápio com base no estoque atual.
- **Resposta**: 200 OK

#### Excluir Item do Cardápio

- **URL**: `/menu/{id}`
- **Método**: DELETE
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Descrição**: Exclui um item do cardápio e todas as suas receitas associadas.
- **Resposta**: 204 No Content

### Receitas

#### Listar Todas as Receitas

- **URL**: `/recipes`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Descrição**: Retorna todas as receitas (relações entre itens do cardápio e itens de estoque).
- **Exemplo de Resposta**:
  ```json
  [
    {
      "id": 1,
      "menuItem": {
        "id": 1,
        "name": "Pizza Margherita",
        "description": "Pizza com molho de tomate, mussarela e manjericão",
        "price": 45.90,
        "available": true,
        "imageUrl": "https://exemplo.com/margherita.jpg",
        "category": "Pizzas"
      },
      "stockItem": {
        "id": 1,
        "name": "Tomate",
        "quantity": 10.50,
        "unit": "kg",
        "minQuantity": 2.00,
        "isLowStock": false,
        "createdAt": "2025-04-04T10:44:56.954",
        "updatedAt": "2025-04-04T10:44:56.954"
      },
      "quantity": 0.2,
      "createdAt": "2025-04-04T10:44:56.954",
      "updatedAt": "2025-04-04T10:44:56.954"
    }
  ]
  ```

#### Buscar Receita por ID

- **URL**: `/recipes/{id}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Descrição**: Retorna uma receita específica pelo seu ID.
- **Exemplo de Resposta**: Similar ao conteúdo do endpoint de listagem.

#### Listar Receitas por Item do Cardápio

- **URL**: `/recipes/menu-item/{menuItemId}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Descrição**: Lista todas as receitas associadas a um item específico do cardápio.
- **Exemplo de Resposta**: Similar ao endpoint de listagem.

#### Listar Receitas por Item de Estoque

- **URL**: `/recipes/stock-item/{stockItemId}`
- **Método**: GET
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN, COZINHA
- **Descrição**: Lista todas as receitas que utilizam um item específico do estoque.
- **Exemplo de Resposta**: Similar ao endpoint de listagem.

#### Criar Nova Receita

- **URL**: `/recipes`
- **Método**: POST
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Corpo da Requisição**:
  ```json
  {
    "menuItemId": 1,
    "stockItemId": 1,
    "quantity": 0.2
  }
  ```
- **Exemplo de Resposta**: A receita criada, similar ao endpoint de busca por ID.

#### Atualizar Receita

- **URL**: `/recipes/{id}`
- **Método**: PUT
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Corpo da Requisição**:
  ```json
  {
    "quantity": 0.25
  }
  ```
- **Exemplo de Resposta**: A receita atualizada.

#### Excluir Receita

- **URL**: `/recipes/{id}`
- **Método**: DELETE
- **Autenticação**: Requer token JWT
- **Perfis Autorizados**: ADMIN
- **Descrição**: Exclui uma receita.
- **Resposta**: 204 No Content

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

### Mesa (RestaurantTable)

| Campo             | Tipo     | Descrição                                         |
|-------------------|----------|---------------------------------------------------|
| id                | Long     | Identificador único da mesa                       |
| number            | Int      | Número da mesa (único no sistema)                 |
| capacity          | Int      | Capacidade de pessoas na mesa                     |
| status            | Enum     | Status da mesa (AVAILABLE, OCCUPIED, etc.)        |
| current_order_id  | Long     | Referência ao pedido atual em andamento na mesa   |
| created_at        | Datetime | Data e hora de criação do registro                |
| updated_at        | Datetime | Data e hora da última atualização do registro     |

### Item de Estoque (StockItem)

| Campo        | Tipo     | Descrição                                        |
|--------------|----------|-------------------------------------------------|
| id           | Long     | Identificador único do item de estoque           |
| name         | String   | Nome do item                                     |
| quantity     | Decimal  | Quantidade disponível no estoque                 |
| unit         | String   | Unidade de medida (kg, l, unidade, etc.)         |
| min_quantity | Decimal  | Quantidade mínima desejada no estoque            |
| created_at   | Datetime | Data e hora de criação do registro               |
| updated_at   | Datetime | Data e hora da última atualização do registro    |

### Item do Cardápio (MenuItem)

| Campo        | Tipo     | Descrição                                        |
|--------------|----------|-------------------------------------------------|
| id           | Long     | Identificador único do item do cardápio          |
| name         | String   | Nome do item                                     |
| description  | String   | Descrição detalhada do item                      |
| price        | Decimal  | Preço de venda                                   |
| available    | Boolean  | Indica se o item está disponível para pedidos    |
| image_url    | String   | URL da imagem do item                            |
| category     | String   | Categoria do item (Pizzas, Bebidas, etc.)        |
| created_at   | Datetime | Data e hora de criação do registro               |
| updated_at   | Datetime | Data e hora da última atualização do registro    |

### Receita (Recipe)

| Campo         | Tipo     | Descrição                                        |
|---------------|----------|-------------------------------------------------|
| id            | Long     | Identificador único da receita                   |
| menu_item_id  | Long     | Referência ao item do cardápio                   |
| stock_item_id | Long     | Referência ao item de estoque                    |
| quantity      | Decimal  | Quantidade do item de estoque necessária         |
| created_at    | Datetime | Data e hora de criação do registro               |
| updated_at    | Datetime | Data e hora da última atualização do registro    |

### Pedido (Order)

| Campo        | Tipo     | Descrição                                        |
|--------------|----------|-------------------------------------------------|
| id           | Long     | Identificador único do pedido                   |
| user_id      | Long     | Referência ao usuário que fez o pedido          |
| table_id     | Long     | Referência à mesa (null para delivery)          |
| total        | Decimal  | Valor total do pedido                           |
| status       | Enum     | Status do pedido (PENDING, PREPARING, etc.)     |
| is_delivery  | Boolean  | Indica se é um pedido para entrega              |
| created_at   | Datetime | Data e hora de criação do registro              |
| updated_at   | Datetime | Data e hora da última atualização do registro   |

### Item de Pedido (OrderItem)

| Campo          | Tipo     | Descrição                                        |
|----------------|----------|-------------------------------------------------|
| id             | Long     | Identificador único do item do pedido           |
| order_id       | Long     | Referência ao pedido                            |
| menu_item_id   | Long     | Referência ao item do cardápio                  |
| quantity       | Int      | Quantidade solicitada                           |
| price_at_time  | Decimal  | Preço no momento do pedido                      |
| notes          | String   | Observações (ex: sem cebola)                    |
| created_at     | Datetime | Data e hora de criação do registro              |
| updated_at     | Datetime | Data e hora da última atualização do registro   |

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

### Gestão de Estoque e Cardápio
- Integração completa entre estoque e cardápio
- Atualização automática da disponibilidade dos itens do cardápio com base no estoque
- Sistema de receitas para definir os ingredientes de cada item do cardápio

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

- Gerenciamento de mesas (em desenvolvimento)
  - Modelo de dados implementado
  - Endpoints REST a implementar
- Sistema de pedidos
- Módulo de entrega
- Relatórios

## Resolução de Problemas Comuns

- **Erro 401 Unauthorized**: Verifique se o token JWT é válido e está sendo enviado corretamente no header
- **Erro 403 Forbidden**: Verifique se o usuário tem as permissões necessárias para acessar o recurso
- **Erro de conexão com o banco de dados**: Confirme que o PostgreSQL está rodando na porta correta (5433)
- **Swagger não carrega**: Verifique se a aplicação está rodando e se o caminho está correto (/api/v1/swagger-ui/index.html)

## Histórico de Atualizações

### Versão 0.1.1 (Abril/2025)

- Início da implementação do sistema de mesas
- Adicionado modelo de dados para mesas com capacidade
- Correção de bugs no sistema de receitas

### Versão 0.1.0 (Maio/2025)

- Implementação do sistema de gestão de estoque
- Implementação do sistema de cardápio
- Implementação do sistema de receitas
- Integração entre estoque e cardápio para controle de disponibilidade

### Versão 0.0.1 (Abril/2025)

- Implementação inicial do sistema
- Configuração de banco de dados PostgreSQL
- Sistema de autenticação com JWT
- Registro e login de usuários
- Endpoint de verificação de saúde da API
- Documentação interativa com Swagger 

### Versão 0.2.0 (Maio/2025)

- Implementação do sistema de mesas
- Endpoints para listar, criar, atualizar e excluir mesas
- Controle de status das mesas (disponível, ocupada, reservada, em limpeza)

### Versão 0.3.0 (Junho/2025)

- Implementação do sistema de pedidos
- Endpoints para criar, atualizar status e cancelar pedidos
- Adicionar itens a pedidos existentes
- Cálculo automático de valores
- Vinculação entre pedidos e mesas
- Atualização do estoque quando um pedido é entregue 