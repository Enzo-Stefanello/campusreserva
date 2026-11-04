# Arquitetura do CampusReserva

> **Status:** arquitetura conceitual alinhada ao MVP acadêmico; **nenhuma implementação ou validação técnica foi executada**.  
> **Fontes funcionais:** [requisitos](./requisitos.md) e [RN-01 a RN-20](./regras-de-negocio.md). As regras aprovadas prevalecem sobre exemplos deste documento.

## 1. Objetivo arquitetural

Construir uma aplicação full-stack de portfólio que demonstre domínio de Java, Spring Boot, persistência relacional, segurança, integridade transacional e testes, sem adotar complexidade operacional desnecessária.

O MVP abrange **laboratórios** e **equipamentos físicos individualizados**. Salas de aula convencionais, integrações institucionais, notificações e microsserviços não fazem parte do primeiro escopo.

## 2. Visão de alto nível

\`\`\`mermaid
flowchart LR
    UI[Frontend React + TypeScript] -->|HTTPS / JSON / API REST| API[Backend Java + Spring Boot]
    API --> DB[(PostgreSQL)]
\`\`\`

- **Frontend:** apresenta recursos, disponibilidade, reservas e operações permitidas a cada perfil. Validações de interface ajudam na experiência, mas não substituem a validação no servidor.
- **Backend:** autentica, autoriza, aplica as RN-01 a RN-20, controla transições e verifica invariantes antes de persistir alterações.
- **PostgreSQL:** armazena usuários, recursos, reservas, movimentações, manutenções e histórico administrativo, com restrições e transações compatíveis com as regras.
- **Flyway:** controla migrações versionadas do esquema.
- **Docker Compose:** ambiente local reproduzível, conforme versões a confirmar.

## 3. Estilo: monólito modular

Uma aplicação Spring Boot com módulos internos orientados por responsabilidades, **sem serviços independentes nem comunicação distribuída**.

### Fronteiras conceituais propostas

| Área | Responsabilidade |
|---|---|
| **Identidade e acesso** | Cadastro público como estudante, autenticação, autorização, perfis e provisionamento seguro do primeiro administrador. |
| **Recursos** | Cadastro e consulta de laboratórios e equipamentos individuais, ativação/desativação e manutenção. |
| **Reservas** | Agendamento, disponibilidade temporal, cotas, períodos acadêmicos, cancelamentos, transições, expiração e análise administrativa. |
| **Movimentações de equipamentos** | Retirada e devolução físicas registradas por administrador, verificação de atrasos e retificações limitadas. |

Estas são **fronteiras de responsabilidade, não uma exigência de quatro projetos ou de quatro camadas artificiais**. A estrutura definitiva de pacotes e classes será escolhida ao implementar cada funcionalidade.

### Divisão de responsabilidades dentro do backend

- **Controllers REST:** recebem solicitações, aplicam validações básicas de formato e retornam respostas HTTP.
- **Aplicação / serviços:** coordenam casos de uso, autorização, transações e chamadas entre responsabilidades.
- **Domínio:** concentra regras e validações de estados, intervalos, disponibilidade, cotas e permissões específicas.
- **Persistência:** acessa o PostgreSQL por repositórios Spring Data JPA e migrações Flyway.

Evitar concentrar regras importantes em controllers ou no frontend. Também evitar abstrações genéricas antes de existir necessidade concreta.

## 4. Modelo conceitual

\`\`\`mermaid
erDiagram
    USER ||--o{ RESERVATION : owns
    RESOURCE ||--o{ RESERVATION : receives
    RESOURCE ||--o{ MAINTENANCE_PERIOD : has
    RESERVATION ||--o{ EQUIPMENT_MOVEMENT : records
    RESERVATION ||--o{ ADMINISTRATIVE_ACTION : has
    USER ||--o{ EQUIPMENT_MOVEMENT : registers
    USER ||--o{ ADMINISTRATIVE_ACTION : performs
\`\`\`

**Entidades e relacionamentos acima são conceituais.** Não fixam quantidade de tabelas, nomes Java, cardinalidades exatas de implementação nem estratégia de herança JPA.

Requisitos do modelo:

- Uma reserva pertence a um titular e referencia um único recurso.
- Cada equipamento físico possui identificação única e histórico próprio, mesmo quando existe outro do mesmo modelo (RN-18).
- Laboratórios e equipamentos possuem comportamentos temporais e estados específicos.
- Movimentações registram responsável administrativo, instantes efetivos e momento de registro.
- Manutenções preservam início, liberação manual, motivo e responsável.
- Retificações preservam valor anterior, valor corrigido, justificativa e responsável (RN-20).
- Reservas afetadas mantêm período e histórico originais mesmo quando não bloqueiam a disponibilidade (RN-13).

## 5. Ciclos de vida

**Laboratórios:** \`CONFIRMADA\` e \`CANCELADA\`. A passagem do horário não cria conclusão automática.

**Equipamentos:** \`CONFIRMADA\`, \`EMPRESTADA\`, \`DEVOLVIDA\`, \`CANCELADA\`, \`EM_ANALISE\` e \`ENCERRADA\`.

Motivos de \`ENCERRADA\`: \`NAO_RETIRADA\`, \`NAO_ATENDIDA\` e \`ANALISE_INCONCLUSIVA\`.

- \`EM_ANALISE\` não ocupa cota nem calendário, mas não autoriza entrega física.
- Atraso é condição calculada de empréstimo aberto, não estado adicional.
- Transições finais não são reabertas pelo fluxo comum.
- A máquina de estados, suas condições e os limites de tempo são definidos em [regras-de-negocio.md](./regras-de-negocio.md).

## 6. Disponibilidade e integridade

Antes da confirmação, o backend considera **em conjunto**:

1. Permissão do usuário e elegibilidade do recurso.
2. Horário e duração permitidos, antecedências e fuso institucional.
3. Conflitos com reservas bloqueantes do mesmo recurso.
4. Cota do usuário e impedimentos por empréstimos atrasados.
5. Manutenção, desativação e condições físicas relevantes do equipamento.

Uma consulta de disponibilidade é informativa; **a confirmação exige validação novamente na mesma operação transacional**. Não se aceita dupla reserva ou violação de cota em requisições concorrentes.

A estratégia específica de proteção contra concorrência (bloqueio transacional, restrições do PostgreSQL ou combinação) **ainda será escolhida mediante testes de integração**. Não afirmar garantia de concorrência antes da validação.

Reservas consecutivas e procedimentos de desativação com reservas futuras permanecem casos de borda explicitados nos requisitos.

## 7. Segurança

- Spring Security será introduzido **antes da exposição dos fluxos reais de reservas**.
- Perfis: \`ESTUDANTE\`, \`PROFESSOR\` e \`ADMINISTRADOR\`; a autorização é sempre verificada no backend.
- Cadastro público não permite atribuir perfil privilegiado.
- O primeiro administrador depende de provisionamento inicial seguro.
- Usuários comuns acessam e modificam apenas suas reservas nas condições permitidas.
- Apenas administradores registram movimentações físicas, manutenção, encerramentos e retificações.
- Credenciais, tokens, senhas e segredos nunca devem ser versionados.

A escolha de **sessão/cookie ou token** depende do desenho de implantação e deve ser feita antes da implementação da autenticação; não está definida aqui.

## 8. Tempo e operações assíncronas

- Fuso institucional: \`America/Sao_Paulo\` (RN-15); instantes devem ser representados sem ambiguidade em backend, banco e API.
- Relógio do servidor é a referência das validações de antecedência, cancelamento e expiração.
- A expiração de reservas de equipamentos deve funcionar mesmo quando nenhuma rotina é executada no segundo exato do vencimento; o mecanismo técnico (verificação sob demanda e/ou tarefa agendada) está por decidir.
- Períodos de manutenção e registros físicos históricos exigem rastreabilidade temporal para verificar impedimentos que existiram durante a janela de retirada.

## 9. Qualidade e testes planejados

- **JUnit e Mockito:** regras isoladas de períodos, cotas, autorização e máquina de estados.
- **Testes de integração:** Spring Boot, PostgreSQL e migrações com dados reais de teste.
- **Testes concorrentes:** confirmar que duas requisições disputando o mesmo equipamento ou limite de cota não violam integridade.
- **Testes de segurança:** tentar acessar reservas de terceiros, cadastrar perfis privilegiados e executar ações administrativas sem autorização.
- **Testes de regras temporais:** limites inclusivos/exclusivos, 5 e 30 minutos, 15 minutos de retirada, 336 horas, horário institucional.
- **GitHub Actions:** executar verificações automatizadas quando o projeto estiver implementado.
- **OpenAPI:** documentar a API real, não endpoints ainda inexistentes.

## 10. Decisões técnicas e limites pendentes

**Para definir antes da implementação correspondente:**
- Versões de Java, Spring Boot, PostgreSQL e dependências; compatibilidade e ambiente local.
- Estratégia de autenticação e provisionamento seguro do administrador.
- Estrutura definitiva de tabelas/entidades, forma de representar instantes e restrições de banco.
- Estratégia transacional e de concorrência, validada com testes.
- Processo de expiração, tratamento de erros HTTP e exposição de regras no frontend.

**Casos operacionais do produto ainda em aberto** (sem alterar as RN aprovadas):
- Desativação de recurso que já possui reservas futuras.
- Limite temporal do cancelamento administrativo de laboratório depois do início.
- Movimentações registradas retrospectivamente ou realizadas fora do expediente.
- Eventual intervalo operacional entre reservas consecutivas.

## 11. Estado atual

A arquitetura descrita é **proposta/documentada, não implementada**. O roadmap define a sequência incremental de construção, com segurança antecipada em relação aos fluxos reserváveis.
