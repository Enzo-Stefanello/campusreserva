<h1 align="center">CampusReserva</h1>

<p align="center">
  Plataforma web para reservas de laboratórios universitários e empréstimos de equipamentos.
</p>

<p align="center">
  <strong>Projeto de portfólio full-stack</strong> · <strong>Status: Fase 0 concluída — Fase 1 ainda não iniciada</strong>
</p>

---

## Visão geral

O **CampusReserva** é uma aplicação planejada para centralizar a consulta de disponibilidade e a reserva de **laboratórios** e **equipamentos físicos individualizados** em instituições de ensino.

O projeto é desenvolvido de forma incremental para demonstrar competências em **backend, modelagem e integridade de dados, segurança, testes e arquitetura de software**.

> **Estado atual:** requisitos, regras de negócio e arquitetura conceitual documentados; ambiente local de desenvolvimento verificado. **A aplicação ainda não foi implementada, executada ou testada.**

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
| Backend | **Java 25 LTS**, **Spring Boot 4.1.2**, Spring MVC, Spring Data JPA, Spring Security |
| Banco de dados | **PostgreSQL 18.6**, Flyway |
| Build Java | **Maven 3.9.x** e Maven Wrapper (a adicionar ao backend) |
| Frontend planejado | React, TypeScript, Vite, Node.js 26 LTS |
| Desenvolvimento local | Docker Desktop, Docker Compose e WSL 2 no Windows |
| Testes planejados | JUnit, Mockito, Testcontainers e testes de integração com PostgreSQL |
| Documentação e CI planejadas | OpenAPI e GitHub Actions |
| Autenticação aprovada | **Sessões HTTP com cookies seguros**, proteção CSRF e logout por invalidação de sessão |

As dependências específicas serão incorporadas progressivamente conforme o roadmap. As versões de ferramentas foram verificadas no ambiente local; **isso não equivale a um build integrado do Spring Boot**.

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

A aplicação será construída nas áreas `backend/` e `frontend/`, **quando as respectivas fases começarem**. A documentação atual está em `docs/`:

- [Requisitos funcionais](docs/requisitos.md)
- [Regras de negócio consolidadas — RN-01 a RN-20](docs/regras-de-negocio.md)
- [Arquitetura e decisões técnicas](docs/arquitetura.md)
- [Roadmap incremental e critérios de aceite](docs/roadmap.md)

## Roadmap resumido

- [x] **Fase 0 — Fundação:** regras e documentos consolidados, stack escolhida e ambiente de desenvolvimento verificado.
- [ ] **Fase 1 — Base técnica:** criar backend, migrações, integração inicial com PostgreSQL e autenticação segura.
- [ ] **Fase 2 — Recursos:** cadastro, consulta e manutenção de laboratórios/equipamentos.
- [ ] **Fase 3 — Reservas:** períodos, cotas, disponibilidade, transações e cancelamentos.
- [ ] **Fase 4 — Empréstimos e exceções:** retirada/devolução, atrasos, análise e histórico.
- [ ] **Fase 5 — Frontend:** experiência web com fluxos para os três perfis.
- [ ] **Fase 6 — Portfólio:** testes, CI, documentação, segurança e demonstração.

## Execução local

**O projeto ainda não possui uma aplicação executável.** Na Fase 0, um PostgreSQL 18.6 foi inicializado e consultado com sucesso via Docker Compose em uma **pasta local separada do repositório**. Esse ambiente contém credenciais privadas, não versionadas.

A configuração de execução do backend e do frontend será adicionada ao projeto apenas quando seus módulos forem implementados.

---

**Desenvolvimento:** [Enzo Lauxen Stefanello](https://github.com/Enzo-Stefanello) · [LinkedIn](https://www.linkedin.com/in/enzo-lauxen-stefanello/)
