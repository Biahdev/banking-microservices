# 🏦 Banking Microservice

Sistema bancário com arquitetura de microserviços para criação de contas e processamento de transações financeiras. O Transaction Service valida e registra
transações, publicando eventos no Kafka, enquanto o Account Service consome esses eventos para atualizar saldos.

## 🛠️ Tecnologias

- Java 21 + Spring Boot 3.3+
- Apache Kafka - Comunicação entre serviços
- PostgreSQL - Banco de dados
- Docker - Containerização
- Swagger - Documentação de APIs
- JUnit 5 + Mockito - Testes automatizados com BDD

## 🏛️ Account Service

- Criação e gerenciamento de contas bancárias
- Controle de status da conta (ativo/inativo)
- Gerenciamento de limites de transação
- Atualização de saldos via eventos Kafka
- Notificação de mudanças de limites

### Endpoints

```
POST   /accounts                    - Criar nova conta
GET    /accounts                    - Listar contas
GET    /accounts/{id}               - Consultar conta
PUT    /accounts/{id}               - Atualizar conta
DELETE /accounts/{id}               - Desativar conta
PUT    /accounts/{id}/reactivate    - Reativar conta
GET    /accounts/{id}/limits        - Consultar limites da conta
PUT    /accounts/{id}/limits        - Atualizar limites da conta
```

## 💳 Transaction Service

- Processamento de transferências entre contas
- Validação de saldos e limites antes das transações
- Histórico completo de transações
- Controle de uso de limites diários

### Endpoints

```
POST   /transactions                           - Criar transferência entre contas
GET    /transactions                           - Listar transações
GET    /transactions/{id}                      - Consultar transação
GET    /transactions/account/{accountId}       - Histórico por conta
GET    /transactions/account/{accountId}/extract - Extrato da conta
GET    /transactions/account/{accountId}/usage   - Uso de limites no dia/mês
```

## 🔄 Comunicação entre Serviços

- Transaction → Account: Atualiza saldos após transferência
- Account → Transaction: Notifica mudanças de limites

## 🚀 Como Executar

```bash
# Clonar o repositório
git clone <url-do-repositorio>
cd banking-microservice

# Configurar variáveis de ambiente e editar .env com suas configurações
cp .env-exemple .env

# Subir os serviços
docker-compose up -d

# Verificar se os serviços estão rodando
docker-compose ps
```

### **Documentação do Swagger**

- **Account Service**: http://localhost:8080/swagger-ui.html
- **Transaction Service**: http://localhost:8081/swagger-ui.html

## 🧪 Testes

```bash
# Executar testes do Account Service
cd account-service
./mvnw test

# Executar testes do Transaction Service (quando implementado)
cd transaction-service
./mvnw test
```

## 🛣️ Roadmap

### Concluído

- Setup inicial do projeto Spring Boot
- Configuração do banco de dados Postgres
- **Docker Setup** - Containerização completa do ambiente
- **Account Service** - Implementação das operações CRUD de contas bancárias
- **Account Service** - Testes Unitários e de integração
- **Account Service** - Documentação com Swagger

### Em Desenvolvimento

- **Transaction Service** - Desenvolvimento do processamento de transferências
- **Kafka Integration** - Configuração da comunicação assíncrona entre serviços

### Próximos Passos

- **Limits Management** - Sistema de limites de transação
- **Transaction Service** - Testes unitários e de integração
- **Transaction Service** - Documentação com Swagger
- **Melhorias na validação** de dados e regras de negócio
- **Otimizações de performance** no processamento de eventos

---
⌨️ com ❤️ por [Ana Beatriz](https://github.com/Biahdev)

