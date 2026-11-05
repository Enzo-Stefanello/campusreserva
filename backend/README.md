# Backend — CampusReserva

Backend do CampusReserva, aplicação para gerenciamento de reservas de laboratórios universitários e equipamentos físicos individualizados.

## Tecnologias

- Java 25 LTS
- Spring Boot 4.1.2
- Spring MVC
- Apache Maven 3.9.16, utilizando Maven Wrapper
- JUnit e infraestrutura de testes do Spring Boot

## Pré-requisitos

- JDK 25 instalado e configurado.
- Acesso à internet para baixar dependências na primeira execução.
- Docker Desktop com Docker Compose e suporte a contêineres Linux.

O Maven não precisa estar instalado globalmente, pois o projeto inclui o Maven Wrapper.

## Execução local

Na raiz do repositório, copie [`.env.example`](../.env.example) para `.env` (somente se ainda não existir) e preencha a senha localmente. Esse arquivo contém somente as variáveis `POSTGRES_*`, utilizadas pelo Compose.

```powershell
docker compose config --quiet
docker compose up -d --wait postgres
```

O Spring Boot não carrega `.env` automaticamente. Para iniciar a aplicação com `spring-boot:run`, defina `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` somente no ambiente do terminal que iniciará o processo Java. Use o mesmo banco, usuário e senha configurados nas variáveis `POSTGRES_*`; ajuste a porta da URL JDBC caso altere `POSTGRES_PORT`. No PowerShell, a senha pode ser informada sem gravá-la no histórico:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://127.0.0.1:5432/campusreserva'
$env:SPRING_DATASOURCE_USERNAME = 'campusreserva'
$env:SPRING_DATASOURCE_PASSWORD = Read-Host 'Senha local do PostgreSQL' -MaskInput
cd backend
```

`-MaskInput` requer PowerShell 7. Em outros terminais, configure a variável de senha pelo ambiente de execução da IDE, sem versioná-la.

O banco é publicado somente em `127.0.0.1` e persiste em volume nomeado próprio do projeto. Verifique conflitos de porta com bancos existentes. Alterar as variáveis `POSTGRES_*` não altera usuários ou senhas de um volume já inicializado.

Os comandos abaixo devem ser executados no diretório `backend/`.

**Verificar o ambiente Maven e Java:**

`.\mvnw.cmd -v`

**Compilar, executar os testes e empacotar:**

`.\mvnw.cmd clean verify`

**Iniciar a aplicação:**

`.\mvnw.cmd spring-boot:run`

Após a inicialização, o servidor HTTP estará disponível em `http://localhost:8080`, utilizando a configuração padrão.

Para encerrar a aplicação, pressione `Ctrl + C` no terminal.

## Testes

`ValidationProblemDetailTests` usa `@WebMvcTest` e MockMvc para verificar corpo válido, campo obrigatório inválido, JSON malformado e método HTTP não permitido. Controller e DTO existem somente nos testes. Os erros verificam `application/problem+json` e os campos RFC 9457, usando o tratamento nativo habilitado por `spring.mvc.problemdetails.enabled=true`, sem advice próprio. Essa camada verifica a ausência de DataSource, Flyway e contêineres no contexto e não exige Docker. Para executá-la isoladamente: `.\mvnw.cmd -Dtest=ValidationProblemDetailTests test`.

Os testes com `@SpringBootTest` preservam `contextLoads` e verificam a conexão JDBC com `SELECT 1` e a inicialização do Flyway, incluindo sua tabela técnica de histórico e validação sem migrações de negócio.

Para executar, mantenha o Docker Desktop ativo com contêineres Linux e execute `.\mvnw.cmd clean verify` a partir de `backend/`. São necessários JDK 25 e acesso à internet no primeiro download das dependências e imagens.

O Testcontainers inicia PostgreSQL 18.6 temporário, com porta dinâmica e sem volume de desenvolvimento. O Spring gerencia o ciclo de vida do contêiner; `@ServiceConnection` fornece as conexões JDBC e Flyway com precedência sobre as propriedades do ambiente. Não é necessário iniciar o Compose, criar `.env` ou definir `SPRING_DATASOURCE_*` para os testes; essas variáveis são necessárias apenas para executar a aplicação local. Os testes conferem que ambos os acessos usam a URL do contêiner temporário.

Sem Docker acessível, os testes falham e não são ignorados. Avisos do Flyway sobre ausência de migrações são esperados nesta etapa; nenhuma migração artificial é criada. As validações abaixo são históricas, anteriores à integração JDBC.

Validações realizadas em 04/11/2026, no Windows 11:

- Build Maven concluído com sucesso.
- Teste automatizado executado: 1 teste, 0 falhas.
- Inicialização do servidor Tomcat confirmada.
- Comunicação HTTP verificada na porta 8080.
- Encerramento do servidor verificado.

A rota `/` retorna HTTP 404 porque ainda não existem endpoints de aplicação implementados.

## Estado atual

O backend possui a estrutura técnica inicial, dependências JDBC/PostgreSQL e Flyway configurado para executar na inicialização usando o mesmo DataSource. Sem migrações disponíveis, não há esquema de negócio aplicado; a primeira migração será criada com o primeiro esquema real. A inicialização sem scripts não comprova execução de uma migração.

Ainda não foram implementados:

- Migrações Flyway do esquema real.
- Entidades e regras de negócio.
- Autenticação e autorização.
- Endpoints REST de domínio ou de saúde.

Essas funcionalidades serão desenvolvidas progressivamente, conforme o [roadmap do projeto](../docs/roadmap.md).
