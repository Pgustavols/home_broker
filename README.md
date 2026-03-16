# 📈 Home Broker

Sistema de corretora de ações desenvolvido em Java com interface via terminal. Permite o cadastro de investidores e ações, execução de ordens de compra, depósitos e consulta de carteira, com persistência em banco de dados PostgreSQL.

---

## 🚀 Funcionalidades

**Área Administrativa (Menu Principal)**
- Cadastrar novo investidor
- Cadastrar nova ação na bolsa
- Atualizar preço de uma ação

**Área do Cliente (após login por CPF)**
- Ver painel de ações com cotação atual
- Consultar saldo disponível
- Executar ordem de compra
- Realizar depósito
- Listar ações compradas

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Descrição |
|---|---|---|
| Java | 17 | Linguagem principal |
| Maven | - | Gerenciador de dependências |
| PostgreSQL | - | Banco de dados relacional |
| HikariCP | 7.0.2 | Pool de conexões |
| PostgreSQL JDBC Driver | 42.7.10 | Conector JDBC |
| SLF4J Simple | 2.0.12 | Logging |

---

## 🗄️ Estrutura do Banco de Dados

O projeto utiliza 3 tabelas principais:

**`investidor`** — Armazena os dados dos clientes da corretora.

**`acao`** — Contém o ticker e o preço atual de cada ação listada.

**`ordem_compra`** — Registra cada compra realizada, com referência ao investidor e à ação.

> O banco também conta com uma **trigger** (`tg_mercado_preco_total`) que atualiza automaticamente o `preco_total` das ordens de compra sempre que o preço de uma ação for alterado.

Script de criação das tabelas e da trigger disponível na raiz do repositório.

---

## 📁 Estrutura do Projeto

```
home_broker/
├── src/
│   └── main/java/
│       ├── Main.java                          # Ponto de entrada e menus CLI
│       └── br/com/home_broker/
│           ├── application/
│           │   └── ConnectionFactory.java     # Pool de conexões com HikariCP
│           ├── dao/
│           │   ├── AcaoDAO.java               # Operações de banco para Ação
│           │   ├── InvestidorDAO.java         # Operações de banco para Investidor
│           │   └── OrdemCompraDAO.java        # Operações de banco para Ordem de Compra
│           ├── model/
│           │   ├── Acao.java                  # Entidade Ação
│           │   ├── Investidor.java            # Entidade Investidor
│           │   └── OrdemCompra.java           # Entidade Ordem de Compra
│           └── service/
│               └── CorretoraService.java      # Regras de negócio da corretora
├── config.properties                          # Configurações de conexão com o banco
└── pom.xml                                    # Dependências Maven
```

---

## ⚙️ Como Configurar e Executar

### Pré-requisitos

- Java 17+
- Maven
- PostgreSQL rodando localmente

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/home_broker.git
cd home_broker
```

### 2. Configure o banco de dados

Crie um banco no PostgreSQL chamado `home_broker` e execute o script SQL para criar as tabelas e a trigger.

### 3. Configure as credenciais

Edite o arquivo `config.properties` na raiz do projeto:

```properties
db.url=jdbc:postgresql://localhost:5432/home_broker
db.user=seu_usuario
db.password=sua_senha
```

### 4. Compile e execute

```bash
mvn compile
mvn exec:java -Dexec.mainClass="Main"
```

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas:

- **Model** — Classes POJO que representam as entidades do domínio.
- **DAO (Data Access Object)** — Responsável pelas queries SQL e comunicação direta com o banco.
- **Service** — Contém as regras de negócio (validações, controle de transações com commit/rollback).
- **Main** — Interface com o usuário via terminal (menus e leitura de inputs).

A compra de ações utiliza **transações explícitas** com rollback automático em caso de falha, garantindo a consistência entre o débito do saldo e o registro da ordem.
