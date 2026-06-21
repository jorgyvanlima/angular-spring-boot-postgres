# 🛡️ LNX-JSP Stack: Template de Arquitetura Fullstack Segura & Containerizada

Este repositório fornece um template de arquitetura **Fullstack desacoplada (SPA + API)**, moderno e pronto para produção utilizando Docker e orquestração controlada. O projeto foi projetado seguindo as melhores práticas globais de segurança da informação (OWASP Top 10), com foco estrito na prevenção de ataques como **Cross-Site Scripting (XSS)**, **Força Bruta (Brute Force)**, **MIME Sniffing**, e **Invasões por Exposição de Rede**.

---

## 🚀 Arquitetura de Rede & Fluxo de Dados

Abaixo está o modelo padrão-ouro de isolamento de rede implementado na stack **LNX-JSP**:

```text
                 [ USUÁRIO / INTERNET (Porta 8085) ]
                                 │
                                 ▼
┌────────────────────────── Docker Host ──────────────────────────┐
│                                                                 │
│   [ Contêiner lnx-jsp-frontend (Nginx Gateway) ]                │
│       │                                                         │
│       ├── (Rota: / ) ──► Arquivos estáticos do Angular          │
│       │                                                         │
│       └── (Rota: /api) ──► Proxy reverso interno                 │
│                                  │                              │
│         =========================│=========================     │
│         REDE: frontend_net       ▼ (Comunicação interna)        │
│                    [ Contêiner lnx-jsp-backend (API) ]          │
│                    (Tomcat - Porta interna 8080)                │
│         =========================│=========================     │
│                                  │                              │
│         =========================│=========================     │
│         REDE: backend_net        ▼ (Comunicação interna)        │
│                    [ Contêiner lnx-jsp-db (PostgreSQL) ]        │
│                    (Porta interna 5432)                         │
│         ===================================================     │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📂 Estrutura do Monorepo e Funções de Cada Arquivo

```text
lnx-jsp/
├── backend/                              # Backend da API Spring Boot 3.x
│   ├── src/main/java/com/lnxjsp/backend/
│   │   ├── config/
│   │   │   ├── CorsConfig.java           # Configurações globais de CORS do MVC
│   │   │   ├── SecurityConfig.java       # [NOVO] Políticas do Spring Security (CSP, FrameOptions, CORS)
│   │   │   └── RateLimitingFilter.java   # [NOVO] Filtro servlet de taxa limite por IP (Anti-Brute Force)
│   │   ├── controller/
│   │   │   ├── HealthCheckController.java# Endpoint de status do sistema e banco de dados
│   │   │   └── SystemSettingController.java# Endpoints CRUD para configurações do sistema com @Valid
│   │   ├── exception/
│   │   │   ├── ResourceNotFoundException.java# Exceção personalizada para recursos não encontrados
│   │   │   └── GlobalExceptionHandler.java# Interceptador global de erros (retornos formatados sem vazar stack traces)
│   │   ├── model/
│   │   │   └── SystemSetting.java        # Entidade do banco de dados com validações robustas (NotBlank, Size, Pattern)
│   │   ├── repository/
│   │   │   └── SystemSettingRepository.java# Repositório JPA seguro com queries parametrizadas contra SQL Injection
│   │   ├── service/
│   │   │   ├── SystemSettingService.java # Interface de serviços do negócio
│   │   │   └── SystemSettingServiceImpl.java# Implementação contendo sanitização de entrada com Jsoup contra XSS
│   │   └── BackendApplication.java       # Classe de inicialização principal do Spring Boot
│   ├── src/main/resources/
│   │   ├── application.yml               # Arquivo de configuração de ambiente parametrizado
│   │   └── import.sql                    # Script SQL executado no bootstrap do banco
│   └── pom.xml                           # Gerenciador de dependências Maven do backend
├── frontend/                             # Frontend SPA com Angular 17+
│   ├── src/app/
│   │   ├── core/
│   │   │   ├── models/                   # Interfaces TypeScript tipadas (system-setting.model.ts)
│   │   │   └── services/                 # Serviços HTTP que comunicam via HttpClient (api.service.ts)
│   │   ├── features/
│   │   │   ├── dashboard/                # Componente visual para status da stack
│   │   │   └── settings/                 # Tela administrativa CRUD de parâmetros do sistema
│   │   └── shared/                       # Componentes e diretivas compartilhadas (ex: navbar)
│   ├── nginx.conf                        # Configuração do Nginx Gateway com Cabeçalhos de Segurança (CSP, HSTS)
│   ├── proxy.conf.json                   # Configuração de proxy reverso em ambiente de desenvolvimento local
│   └── package.json                      # Gerenciador de pacotes Node.js e scripts de automação do frontend
├── docker-compose.yml                    # Arquivo de orquestração com redes isoladas e parâmetros Docker
├── backend.Dockerfile                    # Dockerfile multi-stage otimizado para o backend
├── frontend.Dockerfile                   # Dockerfile multi-stage otimizado para o frontend
└── README.md                             # Este guia técnico detalhado
```

---

## 🔒 Controles de Segurança Implementados

### 1. Isolamento de Redes no Docker
- **O que foi feito:** O cluster Docker foi dividido em duas redes isoladas (`frontend_net` e `backend_net`). A `backend_net` possui a flag `internal: true`.
- **Por que isso é seguro:** O contêiner de banco de dados (`lnx-jsp-db`) está exclusivamente na `backend_net`. Ele não possui acesso à internet e não expõe portas ao host. O frontend (`lnx-jsp-frontend`) está apenas na `frontend_net`. É impossível para um invasor no navegador tentar se comunicar diretamente com o PostgreSQL, pois não há rota física ou virtual que os conecte.
- **Portas Ocultadas:** As portas `8080` (Spring Boot) e `5432` (PostgreSQL) não são mapeadas publicamente no host. Apenas a porta `8085` do Nginx está pública.

### 2. Cabeçalhos de Segurança no Nginx & Spring Security
O Nginx (`frontend/nginx.conf`) e o Spring Security (`SecurityConfig.java`) foram endurecidos para enviar os seguintes cabeçalhos em cada resposta HTTP:
- **`Content-Security-Policy` (CSP):** Restringe a execução de scripts e carregamento de fontes unicamente ao domínio de origem (`'self'`), bloqueando injeções inline de scripts maliciosos.
- **`X-Frame-Options: DENY`:** Impede que a aplicação seja renderizada dentro de tags `<frame>`, `<iframe>` ou `<object>` de sites externos, mitigando ataques de **Clickjacking**.
- **`X-Content-Type-Options: nosniff`:** Força o browser a respeitar os tipos MIME declarados, prevenindo que arquivos de texto carregados por usuários sejam executados como código JS.
- **`Referrer-Policy: strict-origin-when-cross-origin`:** Protege a privacidade restringindo o envio de dados de cabeçalhos de referência.
- **`Permissions-Policy`:** Desabilita APIs nativas do browser como câmera, microfone e geolocalização por padrão.

### 3. Prevenção de XSS (Cross-Site Scripting) no Backend
- **O que foi feito:** Adicionamos a biblioteca **Jsoup** (`jsoup`) no backend. Na classe `SystemSettingServiceImpl.java`, antes de salvar qualquer registro no banco de dados, todas as propriedades de string passam pelo método `Jsoup.clean(texto, Safelist.none())`.
- **Por que isso é seguro:** O Angular possui proteção nativa no template, mas isso não impede que payloads maliciosos sejam enviados diretamente por requisições HTTP REST (por ferramentas como Postman) e armazenados no banco. O filtro Jsoup atua como uma segunda camada de defesa (limpeza de entrada), limpando permanentemente qualquer tag HTML/JS (ex: `<script>`, `onerror`, `onload`) antes da persistência no banco (Stored XSS).

### 4. Proteção contra Ataques de Força Bruta (Rate Limiting)
- **O que foi feito:** Desenvolvemos um filtro servlet personalizado (`RateLimitingFilter.java`) integrado ao pipeline de requisições HTTP do Spring.
- **Por que isso é seguro:** O filtro restringe o número máximo de chamadas que um mesmo endereço IP pode fazer à API (padrão: 100 requisições por minuto). 
- **Suporte a Proxy Reverso:** Como o backend roda atrás do Nginx, todas as conexões recebidas pelo Spring Boot possuem o IP interno do contêiner Nginx. Para evitar falsos positivos globais, o filtro lê o cabeçalho **`X-Forwarded-For`** (injetado e validado pelo Nginx) para rastrear o IP externo real do usuário final. Se o limite for excedido, a API retorna instantaneamente `429 Too Many Requests`.

### 5. Validação Rígida de Entradas (Input Validation)
- **O que foi feito:** Utilizamos a especificação JSR-380 (Hibernate Validator) no modelo `SystemSetting.java`.
- **Por que isso é seguro:** A aplicação rejeita strings malformadas, vazias ou fora dos padrões esperados diretamente na borda do controlador (`@Valid`). Por exemplo, a categoria aceita apenas letras maiúsculas e sublinhados (`^[A-Z_]+$`), e a chave de configuração é validada por regex (`^[a-zA-Z0-9_.-]+$`). Tentativas de injetar dados maliciosos retornam `400 Bad Request` sem processamento interno.

---

## ⚙️ Como Executar a Stack Completa com Docker

### Pré-requisitos
Certifique-se de ter instalado em sua máquina:
- **Docker** (v20+)
- **Docker Compose** (v2.x+)

### 1. Inicializar a Stack
Na raiz do projeto (onde está o arquivo `docker-compose.yml`), execute o comando para construir as imagens e subir os containers:

```bash
docker compose up -d --build
```

### 2. Acessar as Aplicações
Uma vez inicializados os containers, você poderá acessar os serviços localmente através dos mapeamentos de portas:

- **Dashboard do Frontend (Angular):** [http://localhost:8085](http://localhost:8085) (Porta 8085)
- **API do Backend (Spring Boot):** Redirecionada internamente por Nginx no caminho `/api/*`. Exemplo de Healthcheck: [http://localhost:8085/api/status](http://localhost:8085/api/status).
- **Banco de Dados (PostgreSQL):** Rodando de forma segura internamente no container `lnx-jsp-db`. 

*Nota: Se precisar de acesso local para manutenção do banco por fora do Docker, você pode descomentar a seção `# ports:` no `docker-compose.yml` mapeando-a para `127.0.0.1:5432:5432`.*

### 3. Parar a Stack
Para encerrar a execução e remover os containers mantendo os volumes de dados do PostgreSQL salvos:

```bash
docker compose down
```

Para remover os volumes e limpar o cache de dados do banco de dados:
```bash
docker compose down -v
```
