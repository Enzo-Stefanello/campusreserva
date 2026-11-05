# CampusReserva

**Plataforma web para reservas de laboratórios universitários e empréstimos de equipamentos.**

**Projeto de portfólio full-stack** · **Status: Fase 1 em andamento — backend inicial validado**

---

## Visão geral

O **CampusReserva** é uma aplicação para centralizar a consulta de disponibilidade e a reserva de **laboratórios** e **equipamentos físicos individualizados** em instituições de ensino.

O projeto é desenvolvido de forma incremental para demonstrar competências em **backend, modelagem e integridade de dados, segurança, testes e arquitetura de software**.

> **Estado atual:** Fase 0 concluída, com requisitos, regras de negócio e arquitetura documentados. A Fase 1 está em andamento: o backend inicial em Spring Boot foi implementado, compilado, executado e validado com um teste automatizado. A integração com PostgreSQL, a autenticação e as funcionalidades de negócio ainda não foram implementadas.

### Escopo aprovado do MVP

- **Estudante:** consulta recursos, reserva equipamentos e gerencia as próprias reservas conforme as regras.
- **Professor:** reserva laboratórios e equipamentos.
- **Administrador:** gerencia recursos, permissões e manutenções; registra retirada/devolução e trata exceções administrativas.
- **Reservas:** confirmação imediata de solicitações válidas, prevenção de conflitos, horários e prazos específicos, cotas e cancelamentos controlados.
- **Equipamentos:** cada unidade física possui identificação e histórico próprios, com controle de empréstimos e devoluções.

**Fora do MVP:** salas de aula convencionais, integrações com sistemas acadêmicos, notificações automáticas, pagamentos e arquitetura de microsserviços.

Os detalhes e casos operacionais de borda estão na [especificação RN-01 a RN-20](docs/regras-de-negocio.md).

## Stack escolhida

| Camada | Tecnologias e decisões |
| --- | --- |
| Backend | **Java 25 LTS**, **Spring Boot 4.1.2**, Spring MVC; Spring Data JPA e Spring Security planejados |
| Banco de dados | **PostgreSQL 18.6** e Flyway — integração planejada |
| Build Java | **Maven 3.9.16** e Maven Wrapper — implementados no backend |
| Frontend planejado | React, TypeScript, Vite, Node.js 26 LTS |
| Desenvolvimento local | Docker Desktop, Docker Compose e WSL 2 no Windows |
| Testes | JUnit e infraestrutura de testes do Spring Boot implementados; Mockito, Testcontainers e testes de integração com PostgreSQL planejados |
| Documentação e CI planejadas | OpenAPI e GitHub Actions |
| Autenticação aprovada | **Sessões HTTP com cookies seguros**, proteção CSRF e logout por invalidação de sessão — implementação pendente |

As dependências são incorporadas progressivamente conforme o roadmap. O backend inicial já foi compilado e testado localmente. A integração com PostgreSQL, Spring Security e os demais componentes permanece planejada para os próximos incrementos.

## Arquitetura

A solução seguirá um **monólito modular** com uma API REST em Spring Boot, frontend React e PostgreSQL.

Responsabilidades conceituais do backend:

- Identidade, autenticação e autorização.
- Cadastro, consulta e manutenção de recursos.
- Regras de disponibilidade, reserva, cotas e cancelamento.
- Movimentação física de equipamentos e tratamento de exceções.

A proteção contra dupla reserva e violação de cotas será implementada e **testada sob concorrência** no backend/banco, não delegada à interface.

Veja [docs/arquitetura.md](docs/arquitetura.md).

## Organização e documentação

O repositório está organizado para o desenvolvimento incremental da aplicação:

- [`backend/`](backend/) — aplicação Spring Boot inicializada e testada localmente.
- [`frontend/`](frontend/) — área reservada para o frontend planejado.
- [`docs/`](docs/) — requisitos, regras de negócio, arquitetura e planejamento.

Documentação principal:

- [Requisitos funcionais](docs/requisitos.md)
- [Regras de negócio consolidadas — RN-01 a RN-20](docs/regras-de-negocio.md)
- [Arquitetura e decisões técnicas](docs/arquitetura.md)
- [Roadmap incremental e critérios de aceite](docs/roadmap.md)

## Roadmap resumido

- [x] **Fase 0 — Fundação:** regras e documentos consolidados, stack escolhida e ambiente de desenvolvimento verificado.
- [ ] **Fase 1 — Base técnica (em andamento):** backend inicial criado e validado; migrações, integração com PostgreSQL, validações e autenticação segura ainda pendentes.
- [ ] **Fase 2 — Recursos:** cadastro, consulta e manutenção de laboratórios/equipamentos.
- [ ] **Fase 3 — Reservas:** períodos, cotas, disponibilidade, transações e cancelamentos.
- [ ] **Fase 4 — Empréstimos e exceções:** retirada/devolução, atrasos, análise e histórico.
- [ ] **Fase 5 — Frontend:** experiência web com fluxos para os três perfis.
- [ ] **Fase 6 — Portfólio:** testes, CI, documentação, segurança e demonstração.

O progresso detalhado e os critérios de aceite estão disponíveis no [roadmap do projeto](docs/roadmap.md).

## Execução local

O backend inicial já pode ser compilado, testado e executado localmente.

**Pré-requisito:** JDK 25 instalado e configurado.

No Windows, a partir da raiz do repositório:

```powershell
cd backend
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

O servidor HTTP inicia, por padrão, em `http://localhost:8080`.

A rota `/` retorna HTTP 404 neste estágio, pois ainda não existem endpoints de aplicação implementados.

Para instruções detalhadas, consulte o [README do backend](backend/README.md).

### Validações realizadas

O primeiro incremento do backend foi validado localmente no Windows 11:

- Compilação e empacotamento Maven concluídos com sucesso.
- Teste automatizado de carregamento do contexto Spring aprovado: **1 teste, 0 falhas**.
- Inicialização do servidor Tomcat confirmada.
- Comunicação HTTP verificada na porta 8080.
- Encerramento da aplicação verificado.

Essas validações não representam testes de integração com banco de dados nem testes completos das futuras funcionalidades de negócio.

Na Fase 0, o PostgreSQL 18.6 também foi inicializado e consultado com sucesso via Docker Compose em uma **pasta local separada do repositório**. Esse ambiente contém credenciais privadas, não versionadas, e ainda não está integrado à aplicação Spring Boot.

---

**Desenvolvimento:** [Enzo Lauxen Stefanello](https://github.com/Enzo-Stefanello) · [LinkedIn](https://www.linkedin.com/in/enzo-lauxen-stefanello/)