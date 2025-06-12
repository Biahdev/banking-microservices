# 🏦 Banking Microservice

Sistema bancário com arquitetura de microserviços para criação de contas e processamento de transações financeiras. O Transaction Service valida e registra
transações, publicando eventos no Kafka, enquanto o Account Service consome esses eventos para atualizar saldos.

## 🛠️ Tecnologias

- **Java 21** com Spring Boot 3.3+ - Framework base
- **Apache Kafka** - Message broker para eventos distribuídos
- **Postgres** - Persistência de dados com isolamento por serviço
- **Docker & Docker Compose** - Containerização e orquestração
- **Swagger/OpenAPI** - Documentação automática das APIs
- **MapStruct** - Mapeamento objeto-relacional
- **JUnit 5 + Mockito** - Testes automatizados com BDD

## 🔒 Account Service (Porta 8080)

```
POST   /accounts                    - Criar conta
GET    /accounts                    - Listar contas
GET    /accounts/{id}               - Consultar conta
PUT    /accounts/{id}               - Atualizar conta
DELETE /accounts/{id}               - Desativar conta
PUT    /accounts/{id}/reactivate    - Reativar conta
```

## 🛣️ Roadmap

### ✅ Concluído

- Setup inicial do projeto Spring Boot
- Configuração do banco de dados Postgres
- **Docker Setup** - Containerização completa do ambiente
- **Account Service** - Implementação das operações CRUD de contas bancárias

### 🔄 Em Desenvolvimento

- **Account Service** - Testes Unitários e de integração
- **Account Service** - Documentação com Swagger

### 📋 Próximos Passos

- **Transaction Service** - Desenvolvimento do processamento de transações
- **Kafka Integration** - Configuração da comunicação assíncrona entre serviços
- API Gateway para roteamento


