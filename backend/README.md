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

O Maven não precisa estar instalado globalmente, pois o projeto inclui o Maven Wrapper.

## Execução local

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

O projeto possui um teste inicial utilizando `@SpringBootTest`, responsável por verificar o carregamento do contexto Spring.

Validações realizadas em 04/11/2026, no Windows 11:

- Build Maven concluído com sucesso.
- Teste automatizado executado: 1 teste, 0 falhas.
- Inicialização do servidor Tomcat confirmada.
- Comunicação HTTP verificada na porta 8080.
- Encerramento do servidor verificado.

A rota `/` retorna HTTP 404 porque ainda não existem endpoints de aplicação implementados.

## Estado atual

O backend possui somente a estrutura técnica inicial.

Ainda não foram implementados:

- Integração com PostgreSQL e migrações Flyway.
- Entidades e regras de negócio.
- Autenticação e autorização.
- Endpoints REST de domínio ou de saúde.

Essas funcionalidades serão desenvolvidas progressivamente, conforme o [roadmap do projeto](../docs/roadmap.md).