# Roadmap do CampusReserva

> Planejamento incremental para um **MVP acadêmico de portfólio**. Uma etapa só estará concluída após verificação de seus critérios de aceite.  
> Referências: [requisitos](./requisitos.md), [RN-01 a RN-20](./regras-de-negocio.md) e [arquitetura](./arquitetura.md).

## Fase 0 — Fundação e planejamento (concluída)

- [x] Definir visão e posicionamento acadêmico do produto.
- [x] Criar documentação inicial do repositório.
- [x] Revisar e consolidar RN-01 a RN-20 como linha de base funcional do MVP.
- [x] Atualizar os requisitos funcionais e separar casos de borda não aprovados.
- [x] Alinhar a proposta de arquitetura e a sequência do roadmap às regras publicadas.
- [x] Selecionar a stack e verificar as ferramentas no Windows 11: Java 25, Maven 3.9.16, WSL 2, Docker/Compose, PostgreSQL 18.6, Git, Node.js/npm e VS Code.
- [x] Validar o PostgreSQL 18.6 em contêiner (healthcheck e consulta SQL por `psql`) e clonar o repositório na branch `main` com árvore de trabalho limpa.
- [x] Confirmar a presença de `arquitetura.md`, `regras-de-negocio.md`, `requisitos.md` e `roadmap.md` na cópia local.
- [x] Registrar escolhas técnicas iniciais: Java 25 LTS, Spring Boot 4.1.2, PostgreSQL 18.6, Maven 3.9.x com Wrapper e autenticação por sessão HTTP com proteção CSRF.
- [x] Documentar que o projeto ainda não possui backend ou frontend executável; testes de autenticação, integrações e concorrência ficam para as fases de implementação.

**Entrega da Fase 0:** baseline funcional aprovada, arquitetura conceitual e roadmap documentados, stack escolhida e ferramentas locais verificadas. **Não foram realizados build do Spring Boot, testes da aplicação ou integração Spring Boot ↔ PostgreSQL.** O Maven Wrapper, as dependências do projeto e a autenticação serão implementados na Fase 1.

## Fase 1 — Backend inicial, banco e acesso seguro (em andamento)

- [x] Criar a aplicação Spring Boot com versões verificadas.
  - Backend inicializado com Java 25, Spring Boot 4.1.2 e Maven Wrapper 3.9.16.
  - Build e empacotamento executados com sucesso.
  - Teste de carregamento do contexto Spring aprovado (1 teste, 0 falhas).
  - Inicialização do servidor e comunicação HTTP verificadas localmente.
- [ ] Preparar PostgreSQL local (preferencialmente com Docker Compose) e Flyway.
- [ ] Configurar validação de entradas e tratamento padronizado de erros.
- [ ] Criar endpoint simples de saúde e instruções de execução.
- [ ] Implementar cadastro e autenticação mínimos com Spring Security por sessões HTTP, logout com invalidação e proteção CSRF.
- [ ] Verificar cookies seguros, integração React/API na mesma origem quando aplicável e tratamento de sessões expiradas.
- [ ] Estabelecer perfis `ESTUDANTE`, `PROFESSOR`, `ADMINISTRADOR`, proibir autoatribuição privilegiada e definir inicialização segura do primeiro administrador.
- [ ] Testar autenticação, autorização básica, persistência e migrações.

**Aceite:** backend inicia localmente; banco e migrações funcionam; autenticação e rotas protegidas são testadas; credenciais não são versionadas.

## Fase 2 — Recursos e disponibilidade básica

- [ ] Modelar laboratórios e equipamentos físicos individualizados.
- [ ] Implementar listagem, cadastro, atualização e desativação controlada de recursos.
- [ ] Implementar grade acadêmica de laboratórios e horários de equipamentos.
- [ ] Expor consulta inicial de disponibilidade com validação no servidor.
- [ ] Adicionar manutenção por tempo indeterminado com liberação administrativa.
- [ ] Testar cadastro, identificação única, horários e autorização administrativa.

**Aceite:** recursos são persistidos e consultados; equipamentos distintos permanecem independentes; manutenção bloqueia novas reservas; operações administrativas são protegidas.

**Antes do fluxo de desativação com reservas futuras:** resolver o caso de borda documentado nas RN.

## Fase 3 — Reservas e cancelamentos

- [ ] Criar reserva de laboratório com blocos de 45 ou 90 minutos e cota de professor.
- [ ] Criar reserva de equipamento com duração, início e antecedências aprovadas.
- [ ] Aplicar confirmação imediata, disponibilidade transacional e proteção contra conflitos.
- [ ] Aplicar cotas independentes e permitir equipamentos diferentes no mesmo horário.
- [ ] Implementar consulta de reservas próprias e cancelamento nas condições da RN-10.
- [ ] Testar limites temporais, horários, perfis, conflito e requisições concorrentes.

**Aceite:** reserva válida confirma; reserva inválida retorna erro compreensível; conflito e cota não são violados por concorrência; cancelamentos liberam horário corretamente.

## Fase 4 — Empréstimos físicos e exceções administrativas

- [ ] Implementar retirada e devolução registradas exclusivamente por administrador.
- [ ] Aplicar tolerância de 15 minutos, expiração por não retirada e bloqueio por atraso.
- [ ] Registrar impedimentos históricos na janela de retirada e encaminhamento a `EM_ANALISE`.
- [ ] Implementar encerramentos `NAO_ATENDIDA` e `ANALISE_INCONCLUSIVA`, com autoria e justificativa.
- [ ] Preservar distinção entre bloqueio de reserva, disponibilidade física e cota.
- [ ] Implementar retificações administrativas permitidas, com histórico antes/depois.
- [ ] Testar transições válidas/inválidas, concorrência, manutenção, expiração e retificações.

**Aceite:** movimentações alteram estados corretamente; não há falso não comparecimento diante de impedimento conhecido; usuários com empréstimos atrasados sofrem as restrições previstas; histórico é rastreável.

**Antes dos casos de borda:** decidir movimentações retrospectivas/fora do expediente, margem entre empréstimos consecutivos e cancelamentos administrativos após início.

## Fase 5 — Frontend e experiência de uso

- [ ] Inicializar React, TypeScript e Vite com versões verificadas.
- [ ] Implementar autenticação, listagem e disponibilidade.
- [ ] Implementar criação, consulta e cancelamento de reservas.
- [ ] Criar interface administrativa mínima para recursos, manutenção, retirada/devolução e análise.
- [ ] Tratar carregamento, sucesso, validação, conflito, acesso negado e estados de reserva.
- [ ] Validar os fluxos integrados mais importantes.

**Aceite:** estudante, professor e administrador conseguem realizar seus fluxos autorizados no navegador; ações proibidas são negadas pelo backend.

## Fase 6 — Qualidade e entrega de portfólio

- [ ] Consolidar testes unitários, integração com PostgreSQL e cenários concorrentes.
- [ ] Configurar GitHub Actions para executar testes reais; não marcar CI como validada quando apenas compila sem testes.
- [ ] Revisar segurança, variáveis de ambiente, mensagens de erro e migrações.
- [ ] Gerar documentação OpenAPI e instruções reproduzíveis.
- [ ] Registrar exemplos de uso e capturas de tela **reais**.
- [ ] Avaliar deploy demonstrável, considerando custo e simplicidade.
- [ ] Revisar README e documentação final, distinguindo implementado de planejado.

**Aceite:** aplicação demonstrável, fluxos testados, documentação coerente com o código e configuração reproduzível.

## Método de execução

1. Trabalhar em **incrementos pequenos e verificáveis**, com objetivo e critério de aceite antes de escrever código.
2. Validar as versões e dependências antes de incluí-las no projeto.
3. Explicar e registrar consequências de mudanças relevantes em código, dependências ou configurações.
4. Não presumir execução ou sucesso de funcionalidades que ainda não foram testadas.
5. Não avançar automaticamente entre etapas sem alinhamento.