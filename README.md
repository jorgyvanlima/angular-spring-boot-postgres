# LNX-JSP Stack: Template de Arquitetura Fullstack Containerizada

Este repositório fornece um template de arquitetura Fullstack robusto, moderno e totalmente pronto para produção utilizando Docker e Docker Compose. O ambiente é denominado **LNX-JSP**.

## 🚀 Tecnologias Utilizadas

- **Frontend:** Angular 17+ (Servido via Nginx Alpine em produção)
- **Backend:** Spring Boot 3.x (Java 17+, Eclipse Temurin JRE)
- **Banco de Dados:** PostgreSQL 16 (Alpine)
- **Orquestração & DevOps:** Docker & Docker Compose

---

## 📁 Arquitetura de Diretórios do Monorepo

```text
lnx-jsp/
├── backend/                  # Código-fonte do Spring Boot
│   ├── src/                  # Código Java e configurações
│   └── pom.xml               # Dependências do Maven
├── frontend/                 # Código-fonte do Angular 17+
│   ├── src/                  # Componentes e folhas de estilo
│   ├── angular.json          # Configuração do Angular CLI
│   ├── package.json          # Dependências do Node
│   ├── proxy.conf.json       # Proxy reverso de Desenvolvimento
│   └── nginx.conf            # Servidor e proxy reverso de Produção (Docker)
├── docker-compose.yml        # Configuração do cluster Docker
├── backend.Dockerfile        # Dockerfile Multi-stage para backend
├── frontend.Dockerfile       # Dockerfile Multi-stage para frontend
└── README.md                 # Este documento de referência
```

---

## ⚙️ Como Executar a Stack Completa com Docker

### Pré-requisitos
Certifique-se de ter instalado em sua máquina:
- **Docker** (v20+)
- **Docker Compose** (v2.x+)

### 1. Inicializar a Stack
Na raiz do projeto (onde está o arquivo `docker-compose.yml`), execute o comando para construir as imagens e subir os containers:

```bash
docker compose up --build
```

### 2. Acessar as Aplicações
Uma vez inicializados os containers, você poderá acessar os serviços localmente através dos mapeamentos de portas:

- **Dashboard do Frontend (Angular):** [http://localhost](http://localhost) (Porta 80)
- **API do Backend (Spring Boot):** [http://localhost:8080/api/status](http://localhost:8080/api/status) (Porta 8080)
- **Banco de Dados (PostgreSQL):** `localhost:5432` (Credenciais configuradas no `docker-compose.yml`)

### 3. Monitorar Logs
Para inspecionar os logs de execução de todos os containers em tempo real:

```bash
docker compose logs -f
```

Ou isoladamente por container:
```bash
docker compose logs -f lnx-jsp-backend
docker compose logs -f lnx-jsp-frontend
docker compose logs -f lnx-jsp-db
```

### 4. Parar a Stack
Para encerrar a execução e remover os containers mantendo os volumes persistentes salvos:

```bash
docker compose down
```

Para remover os volumes e limpar o cache de dados:
```bash
docker compose down -v
```

---

## 🛠️ Desenvolvimento Local (Sem Docker)

Se desejar alterar os códigos localmente e testar em tempo real sem rebuildar os containers Docker:

### ☕ Rodando o Backend (Spring Boot)
1. Instale o Java 17+ em sua máquina.
2. Certifique-se de ter um banco PostgreSQL local rodando com as credenciais padrões do `application.yml` ou configure as variáveis de ambiente necessárias.
3. Acesse a pasta `backend/` e execute:
   ```bash
   mvnw spring-boot:run
   ```

### 🅰️ Rodando o Frontend (Angular)
1. Certifique-se de ter o Node.js v20 instalado.
2. Acesse a pasta `frontend/` e instale as dependências:
   ```bash
   npm install
   ```
3. Inicie o servidor de desenvolvimento com o proxy integrado:
   ```bash
   npm start
   ```
   *(Este comando roda `ng serve --proxy-config proxy.conf.json` direcionando chamadas de `/api/*` para o backend rodando em `localhost:8080`)*
4. Acesse a aplicação em [http://localhost:4200](http://localhost:4200).

---

## 🔒 Boas Práticas Adotadas

1. **Construção Multi-stage (Dockerfiles):** Garante imagens finais leves e seguras eliminando compiladores e pacotes de build redundantes no runtime final.
2. **Healthcheck nativo:** O backend só inicia após o container do PostgreSQL estar aceitando conexões de forma saudável.
3. **Gerenciamento de Segredos por Env:** Credenciais e rotas não estão expostas no código duro, mas sim injetadas via variáveis do Docker.
4. **Proxy Reverso:** O frontend atua como proxy reverso para a API, centralizando requisições e resolvendo problemas de CORS.
