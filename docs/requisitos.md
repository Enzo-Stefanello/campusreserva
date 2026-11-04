# Requisitos do CampusReserva

> **Status:** requisitos funcionais do MVP acadêmico definidos; funcionalidades ainda não implementadas nem validadas por testes.  
> **Referência normativa:** [RN-01 a RN-20 — regras de negócio consolidadas](./regras-de-negocio.md) (versão 1.0 de 04/11/2026). Em caso de dúvida sobre prazos, permissões, transições ou exceções, consultar o documento de regras de negócio.

## 1. Objetivo e escopo

Permitir que uma instituição de ensino administre **laboratórios e equipamentos físicos individualizados**, oferecendo consulta de disponibilidade, reservas confirmadas imediatamente e controle administrativo da retirada e devolução de equipamentos.

**Incluído no MVP:** laboratórios, equipamentos, autenticação, autorização, regras de reserva e cancelamento, cotas, acompanhamento de empréstimos, manutenção, tratamento de indisponibilidade e retificações administrativas limitadas.

**Fora do MVP:** salas de aula convencionais, integração com calendário e sistemas acadêmicos, notificações automáticas, pagamentos, agendamento de manutenção e microsserviços.

O projeto tem **escala acadêmica e objetivo de portfólio**. As decisões priorizam clareza de domínio, segurança e verificabilidade sem complexidade desnecessária.

## 2. Perfis e permissões

| Perfil | Responsabilidades principais |
|---|---|
| `ESTUDANTE` | Consultar recursos, reservar equipamentos, acompanhar e cancelar as próprias reservas dentro das regras. |
| `PROFESSOR` | Além das ações sobre equipamentos, reservar laboratórios e gerenciar as próprias reservas. |
| `ADMINISTRADOR` | Gerenciar recursos, atribuir perfis, registrar retiradas e devoluções, manutenções, análises, cancelamentos excepcionais e retificações. |

- Cadastro público cria somente `ESTUDANTE`; atribuições privilegiadas são controladas administrativamente.
- Primeira conta administrativa é provisionada por procedimento inicial seguro, sem credenciais no repositório.
- O backend valida as permissões e impede acesso indevido a reservas de terceiros.
- Apenas administradores registram movimentações físicas de equipamentos.

## 3. Requisitos funcionais do MVP

| ID | Requisito | Critério de aceitação de alto nível |
|---|---|---|
| RF-01 | Consultar recursos | Usuários consultam laboratórios e unidades físicas de equipamentos; recursos indisponíveis são identificáveis. |
| RF-02 | Gerenciar recursos | Administrador cadastra, atualiza, desativa e identifica individualmente os recursos, preservando histórico. |
| RF-03 | Consultar disponibilidade | O backend considera horários, reservas bloqueantes, manutenção e empréstimos físicos abertos. |
| RF-04 | Criar reservas | Solicitação válida é confirmada imediatamente, com validação de perfil, período, antecedência, cota e disponibilidade. |
| RF-05 | Impedir sobreposição e violações de cota | Solicitações simultâneas não confirmam reservas conflitantes da mesma unidade física nem ultrapassam cotas. |
| RF-06 | Consultar minhas reservas | Usuário autenticado visualiza suas reservas, períodos e estados pertinentes. |
| RF-07 | Cancelar reservas | Titular e administrador cancelam reservas somente conforme prazos, permissões e exceções aprovadas. |
| RF-08 | Autenticar usuários | Operações protegidas exigem autenticação; cadastro público não atribui papéis privilegiados. |
| RF-09 | Autorizar operações por perfil | Estudante não reserva laboratório; movimentações, manutenção e decisões administrativas exigem perfil autorizado. |
| RF-10 | Registrar retirada e devolução | Administrador registra horário efetivo, equipamento e responsável; movimentações válidas alteram o estado da reserva. |
| RF-11 | Controlar prazos e atrasos | Há tolerância de retirada de 15 minutos, expiração por não retirada e bloqueios por empréstimos atrasados. |
| RF-12 | Tratar indisponibilidade e manutenção | Manutenção bloqueia novas reservas; impedimentos durante a tolerância levam reservas afetadas a análise sem imputar ausência indevida. |
| RF-13 | Encerrar análises administrativas | Administração encerra como não atendida ou inconclusiva, com justificativa e histórico, sem reativação pelo fluxo comum. |
| RF-14 | Retificar registros administrativos | Retificações justificadas preservam valores anteriores, autoria e cronologia, sem desfazer transições de estado. |
| RF-15 | Aplicar cotas independentes | Equipamentos: até 3 compromissos contáveis por usuário; laboratórios: até 5 reservas futuras por professor. |
| RF-16 | Controlar perfis | Alterações de perfil são autorizadas; a primeira conta administrativa não depende de autoatribuição pública. |

## 4. Síntese das restrições

| Tema | Regra aprovada |
|---|---|
| Laboratórios | Professores; períodos de 45 ou 90 minutos conforme grade acadêmica; segunda a sábado. |
| Equipamentos | Estudantes e professores; duração exata de 1, 2, 3 ou 4 horas; início em :00 ou :30; segunda a sábado, das 08:00 às 22:00, no mesmo dia. |
| Antecedência mínima | 5 minutos para laboratórios; 30 minutos para equipamentos. |
| Antecedência máxima | 336 horas exatas até o início previsto. |
| Fuso institucional | `America/Sao_Paulo`. |
| Cancelamento comum | Até 15 minutos antes em laboratório; até 30 minutos antes em equipamento, ressalvada tolerância excepcional aprovada. |
| Tolerância de retirada | Até 15 minutos após o início previsto, inclusive. |
| Reservas simultâneas | Permitidas para equipamentos físicos distintos, respeitada a cota. |
| Manutenção | Por tempo indeterminado, com liberação manual do administrador. |

Os detalhes, exceções, histórico e critérios específicos constam em [regras-de-negocio.md](./regras-de-negocio.md).

## 5. Ciclos de vida aprovados

- **Laboratório:** `CONFIRMADA`, `CANCELADA`. O término do período não cria estado persistido de conclusão.
- **Equipamento:** `CONFIRMADA`, `EMPRESTADA`, `DEVOLVIDA`, `CANCELADA`, `EM_ANALISE`, `ENCERRADA`.
- Motivos de `ENCERRADA`: `NAO_RETIRADA`, `NAO_ATENDIDA`, `ANALISE_INCONCLUSIVA`.
- `EM_ANALISE` não ocupa cota nem bloqueia o intervalo original por si só; disponibilidade física continua dependendo do equipamento e de suas restrições.
- Atraso é condição calculada de empréstimo aberto, não novo estado.
- Estados finais não são reativados pelo fluxo comum.

## 6. Requisitos não funcionais

- Organização do backend por responsabilidades claras, com testes automatizados para regras críticas.
- Persistência relacional em PostgreSQL e migrações versionadas.
- Validações no servidor, proteção contra condições de corrida e integridade transacional.
- Autenticação e autorização aplicadas no backend; segredos e credenciais fora do código versionado.
- Tratamento consistente de erros de validação, conflito e acesso negado.
- Documentação de API, execução reproduzível e interface responsiva.
- Testes unitários, de integração e de concorrência para transições e regras relevantes.

## 7. Pontos de borda documentados — não são novas decisões aprovadas

O fechamento funcional das RN-01 a RN-20 não resolve automaticamente todos os cenários operacionais. Antes de implementar as operações correspondentes, definir:

1. Tratamento de reservas futuras quando um recurso for desativado definitivamente.
2. Limite temporal do cancelamento administrativo excepcional de laboratórios após o início do período.
3. Registro de movimentações físicas fora do expediente e lançamentos retrospectivos.
4. Necessidade ou não de intervalo operacional entre empréstimos consecutivos.

A estratégia de transações, esquema definitivo de dados, mecanismo de autenticação, processamento de expirações e representação técnica de instantes serão escolhidos e testados durante o projeto técnico, não considerados funcionalidades já existentes.

## 8. Situação da implementação

- **Regras de negócio:** consolidadas e aprovadas em nível funcional, com casos de borda explicitados.
- **Código, migrações e testes:** ainda não executados nesta etapa documental.
- **Arquitetura e roadmap:** documentos iniciais a revisar em etapa posterior para alinhamento com este escopo.
