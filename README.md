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

- **Autenticação**:
  - `POST /auth/register` - Registro de novos usuários
  - `POST /auth/login` - Login e obtenção de token JWT

- **Diagnóstico**:
  - `GET /health` - Verificação do status da API

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

## Contribuindo

1. Clone o repositório
2. Crie uma branch para sua funcionalidade (`git checkout -b feature/nova-funcionalidade`)
3. Faça commit das alterações (`git commit -m 'Adiciona nova funcionalidade'`)
4. Envie para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## Contato

Samir Mamede - [anuarsamir@gmail.com](mailto:anuarsamir@gmail.com)

Link do projeto: [https://github.com/SamirMamede/wakari-back-end](https://github.com/SamirMamede/wakari-back-end) 