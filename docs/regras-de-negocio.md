# CampusReserva — Regras de negócio consolidadas (RN-01 a RN-20)

**Versão:** 1.0 — 04/11/2026  
**Status:** decisões de negócio aprovadas e documentadas; implementação e testes ainda pendentes.  
**Objetivo:** especificar o MVP acadêmico e fornecer critérios observáveis para futura modelagem e testes.

> Este documento consolida decisões tomadas após a documentação inicial do repositório. Não representa código existente ou testes executados. Os pontos expressamente indicados como **pendentes** não são decisões aprovadas.

## 1. Escopo e princípios gerais

- O MVP contempla **laboratórios** e **equipamentos físicos individuais**. Salas de aula convencionais ficam fora do MVP.
- Perfis: `ESTUDANTE`, `PROFESSOR` e `ADMINISTRADOR`.
- Reservas válidas são **confirmadas imediatamente**, sem fila de aprovação.
- Toda reserva está associada a um usuário, a um recurso físico e a um período previsto com início anterior ao fim.
- O backend é a autoridade para horários, autorização, cotas, disponibilidade e transições de estado.
- Um recurso não pode receber duas reservas com períodos conflitantes. Reservas diferentes podem encostar no horário-limite sem compartilhar tempo de uso; a convenção técnica de intervalos semifechados `[início, fim)` é recomendada, mas será explicitada na modelagem.
- A consulta de disponibilidade não garante a gravação: todas as verificações relevantes são repetidas na confirmação, com proteção contra concorrência.
- Dados históricos não devem ser apagados apenas porque uma reserva foi cancelada, encerrada ou um recurso foi desativado.
- As regras não pressupõem integração com calendário institucional, sistema acadêmico, notificações ou pagamentos.

## 2. Regras de negócio

### RN-01 — Confirmação imediata e integridade das reservas

1. Uma solicitação válida será confirmada automaticamente, sem aprovação prévia do administrador.
2. A confirmação exige recurso elegível, permissão adequada, período válido, disponibilidade e respeito à cota do usuário.
3. Reservas confirmadas não poderão sobrepor outras reservas bloqueantes do mesmo recurso no período solicitado.
4. O servidor deve validar os requisitos novamente no momento da persistência; duas solicitações concorrentes não podem produzir dupla reserva nem violar cotas.
5. A confirmação inicial não impede que fatos posteriores, como manutenção, afetem a execução física do agendamento.

**Aceitação:** duas solicitações simultâneas para a mesma câmera no mesmo horário não podem ser ambas confirmadas; uma solicitação válida sem conflito é confirmada sem intervenção administrativa.

### RN-02 — Períodos acadêmicos para laboratórios

1. Laboratórios só podem ser reservados em **um período de 45 minutos** ou **dois períodos consecutivos de 45 minutos pertencentes ao mesmo bloco (90 minutos)**.
2. Não é permitido combinar períodos atravessando o intervalo entre blocos.
3. Um professor pode reservar somente um período de um bloco, deixando o outro disponível a outra reserva compatível.
4. A reserva de dois períodos exige disponibilidade em ambos.

**Grade inicial aprovada:**

| Bloco | Primeiro período | Segundo período |
|---|---|---|
| A/B | 08:00–08:45 | 08:45–09:30 |
| C/D | 09:45–10:30 | 10:30–11:15 |
| E/E1 | 11:30–12:15 | 12:15–13:00 |
| F/G | 14:00–14:45 | 14:45–15:30 |
| H/I | 15:45–16:30 | 16:30–17:15 |
| J/K | 17:30–18:15 | 18:15–19:00 |
| L/M | 19:15–20:00 | 20:00–20:45 |
| N/P | 21:00–21:45 | 21:45–22:30 |

**Aceitação:** 08:00–09:30 é válida quando livre; 08:45–10:30 é inválida; 08:00–08:45 e 08:45–09:30 podem pertencer a professores diferentes.

### RN-03 — Dias de reserva dos laboratórios

1. São permitidas reservas de laboratórios de **segunda-feira a sábado**.
2. Domingos não admitem reservas de laboratório.
3. O MVP não integra calendário de feriados acadêmicos. Assim, não há bloqueio automático por feriados que caiam de segunda a sábado.

**Aceitação:** solicitação para domingo é rejeitada; sábado é elegível, observadas as demais regras.

### RN-04 — Antecedência mínima

1. Laboratório: início previsto pelo menos **5 minutos** após o instante da solicitação.
2. Equipamento: início previsto pelo menos **30 minutos** após o instante da solicitação.
3. O instante exatamente no limite é válido; um instante posterior ao limite de solicitação é inválido.
4. A hora do servidor, e não a informada pelo cliente, é a referência.

**Aceitação:** às 13:55, laboratório com início às 14:00 é elegível; às 13:56 não é. Às 13:30, equipamento com início às 14:00 é elegível; às 13:31 não é.

### RN-05 — Perfis, permissões e provisionamento de contas

1. `ESTUDANTE` pode consultar recursos autorizados, reservar equipamentos, consultar seu histórico e cancelar suas próprias reservas quando permitido; não pode reservar laboratórios.
2. `PROFESSOR` pode reservar laboratórios e equipamentos, consultar seu histórico e cancelar suas próprias reservas quando permitido.
3. `ADMINISTRADOR` gerencia recursos, manutenções, movimentações físicas, análises, retificações e cancelamentos excepcionais conforme regras próprias.
4. **Somente administradores** registram retiradas e devoluções físicas.
5. Cadastro público cria apenas conta de `ESTUDANTE`.
6. Mudança de perfil e atribuição de `PROFESSOR` ou `ADMINISTRADOR` são ações controladas por administrador autorizado; o usuário não pode elevar o próprio perfil livremente.
7. A primeira conta administrativa deverá ser provisionada por processo inicial seguro, sem credenciais versionadas.
8. A autorização é verificada no backend e usuários comuns não podem consultar ou alterar dados privados de reservas de terceiros.
9. O administrador pode cancelar excepcionalmente reservas `CONFIRMADA` de laboratórios ou equipamentos mesmo fora do prazo comum, registrando motivo, responsável e horário; isso não substitui o encerramento por indisponibilidade e não se aplica a equipamento já `EMPRESTADA` como um cancelamento comum.

**Aceitação:** estudante não reserva laboratório nem registra devolução; administrador pode elevar estudante a professor, com autorização; solicitação pública de conta administrativa é rejeitada.

### RN-06 — Reserva e acompanhamento de equipamentos

1. Estudantes e professores podem solicitar reservas de unidades físicas de equipamento, de acordo com autorização, disponibilidade, cota e regras temporais.
2. Confirmação de reserva e empréstimo efetivo são acontecimentos distintos: a reserva inicialmente fica `CONFIRMADA` e somente uma retirada administrativa válida a torna `EMPRESTADA`.
3. Usuários conseguem consultar suas próprias reservas, seus estados e o histórico permitido.
4. A indisponibilidade de um equipamento não bloqueia automaticamente outra unidade física do mesmo modelo.

**Aceitação:** uma reserva confirmada não é contabilizada como retirada física; duas câmeras distintas podem estar reservadas no mesmo horário se válidas.

### RN-07 — Registro físico de retirada e devolução

1. Apenas administrador registra a entrega e o recebimento físico de equipamentos.
2. A movimentação preserva equipamento, reserva, administrador, instante efetivo e registro da operação.
3. A retirada válida altera `CONFIRMADA` para `EMPRESTADA`; a devolução válida altera `EMPRESTADA` para `DEVOLVIDA`.
4. Não há devolução sem retirada nem segunda retirada da mesma reserva.
5. Os horários efetivos podem ser corrigidos nos limites da RN-20, sem desfazer a movimentação.
6. A devolução de um empréstimo aberto continua registrável mesmo se o equipamento tiver sido colocado em manutenção.

**Aceitação:** estudante não registra retirada; administrador registra uma devolução e o empréstimo é encerrado; devolução de reserva nunca retirada é rejeitada.

### RN-08 — Duração e devolução prevista dos equipamentos

1. A duração prevista é exatamente **1, 2, 3 ou 4 horas**.
2. Início e término previstos devem ocorrer no **mesmo dia**.
3. Uma retirada efetiva feita depois do horário previsto não prorroga automaticamente o término previsto.
4. Atrasos de devolução não provocam conclusão automática do empréstimo.

**Aceitação:** 09:30–12:30 é válida quanto à duração; 09:30–12:00 é inválida; retirada às 09:40 não desloca término previsto das 12:30.

### RN-09 — Funcionamento do setor de equipamentos

1. Os horários previstos de retirada e devolução devem situar-se entre **08:00 e 22:00**, de segunda-feira a sábado.
2. Um período pode terminar exatamente às 22:00, mas não depois.
3. Não são permitidos agendamentos que terminem no dia seguinte.
4. O MVP não impõe feriados acadêmicos automaticamente nem modela a escala real de funcionários.

**Aceitação:** sábado 21:00–22:00 é válido quanto ao funcionamento; segunda 19:00–23:00 é inválido; domingo é inválido.

### RN-10 — Cancelamento comum e excepcional

1. O titular pode cancelar reserva de laboratório `CONFIRMADA` até **15 minutos antes** de seu início, inclusive.
2. O titular pode cancelar reserva de equipamento `CONFIRMADA` até **30 minutos antes** de seu início, inclusive, e nunca depois da retirada física.
3. Quando, **no instante da confirmação**, faltarem menos de 5 minutos para o fechamento do prazo normal de cancelamento, existe tolerância excepcional de até **5 minutos após a confirmação**, sempre limitada ao instante estritamente anterior ao início previsto. Isso inclui confirmação exatamente no limite normal de cancelamento.
4. Fora desse caso, aplica-se o prazo normal. Cancelamento após o início previsto não é permitido ao titular.
5. Cancelamento preserva histórico e libera a ocupação de horário daquela reserva.
6. Administrador poderá cancelar excepcionalmente uma reserva `CONFIRMADA` após o prazo comum, com justificativa e registro de autoria e momento. Não poderá tratar empréstimo já `EMPRESTADA` como cancelamento comum nem substituir o fluxo RN-13 quando houver impossibilidade de entrega.

**Aceitação:** equipamento confirmado às 13:30 para 14:00 pode ser cancelado excepcionalmente até 13:35, se ainda não retirado; reserva de laboratório às 13:46 para 14:00 pode ser cancelada dentro dos cinco minutos após a confirmação, desde que antes de 14:00.

### RN-11 — Janela de retirada e expiração por não retirada

1. A retirada administrativa de equipamento pode ocorrer desde o início previsto até **15 minutos depois**, inclusive; não há retirada antecipada pelo fluxo normal.
2. Após o fim dessa janela, sem retirada válida, a reserva não pode receber retirada pelo fluxo normal.
3. Antes de classificar a reserva como `ENCERRADA` com motivo `NAO_RETIRADA`, o sistema deve verificar **impedimentos operacionais conhecidos e registrados em qualquer momento de toda a janela**.
4. Impedimentos relevantes incluem equipamento em manutenção que impossibilite entrega e empréstimo físico anterior ainda não devolvido.
5. Sem impedimento conhecido, aplica-se o encerramento por `NAO_RETIRADA` após a tolerância.
6. Com impedimento relevante durante qualquer parte da janela, mesmo já resolvido, a reserva sem retirada é encaminhada para `EM_ANALISE`, não classificada automaticamente como ausência do usuário.
7. Uma retirada válida já registrada impede expiração por não retirada.
8. Processamento tardio da expiração deve produzir o resultado correspondente à situação histórica, sem depender de rotina que execute no segundo exato.

**Aceitação:** equipamento não retirado até após 14:15, sem impedimento, encerra como `NAO_RETIRADA`; equipamento indisponível até 14:10 e sem retirada até após 14:15 vai para `EM_ANALISE`.

### RN-12 — Bloqueio de usuário por empréstimo atrasado

1. Usuário com **qualquer equipamento ainda emprestado e com devolução prevista ultrapassada** não poderá criar novas reservas de equipamento nem retirar outros equipamentos.
2. Reservas já confirmadas permanecem registradas, não sendo automaticamente canceladas.
3. O impedimento termina quando não existir mais empréstimo atrasado e não devolvido do usuário.
4. A restrição não impede o registro da devolução nem bloqueia reservas de laboratório de um professor.
5. A condição é calculada com base no horário previsto e na ausência de devolução; não exige um marcador manual permanente.

**Aceitação:** estudante com câmera atrasada não reserva tripé novo; professor com equipamento atrasado ainda pode reservar laboratório; devolução remove o bloqueio se não houver outro atraso.

### RN-13 — Análise e encerramento por indisponibilidade operacional

1. Quando um impedimento registrado afetar a janela de retirada e não houver retirada efetiva, a reserva poderá seguir para `EM_ANALISE` conforme RN-11; um administrador poderá reconhecer diretamente impossibilidade comprovada de atendimento e encerrar a reserva confirmada quando couber.
2. `EM_ANALISE` não comprova presença nem ausência do titular.
3. `EM_ANALISE` não ocupa a cota do usuário, **não bloqueia novos agendamentos por seu intervalo original** e não permite retirada pelo fluxo normal.
4. Isso não torna o equipamento fisicamente disponível: empréstimos abertos, manutenção e demais reservas bloqueantes continuam relevantes.
5. Somente administrador conclui a análise, registrando justificativa, responsável e momento.
6. Quando for comprovado que a reserva não pôde ser atendida por indisponibilidade, encerra-se com motivo `NAO_ATENDIDA`.
7. Quando houve impedimento registrado, mas a relação causal com o não atendimento não puder ser estabelecida com segurança, encerra-se com motivo neutro `ANALISE_INCONCLUSIVA`.
8. Não se atribui `NAO_RETIRADA` simultaneamente a `NAO_ATENDIDA` ou `ANALISE_INCONCLUSIVA`.
9. Reservas analisadas/encerradas não são reativadas pelo fluxo comum, mesmo após retorno do equipamento; caso precise, o usuário realiza uma nova reserva.
10. O encerramento de uma reserva afetada não altera automaticamente o empréstimo que causou o problema nem outras reservas.

**Aceitação:** câmera devolvida às 14:10 com reserva para 14:00 e nenhuma retirada subsequente: reserva vai para análise após tolerância; conclusão inconclusiva não acusa o usuário; uma reserva em análise não ocupa cota ou calendário por si só.

### RN-14 — Horários de início dos equipamentos

1. Equipamentos só poderão ter início previsto em minutos **:00** ou **:30**.
2. Devem respeitar as durações da RN-08, dias e horários da RN-09 e os demais limites temporais.
3. O término previsto deve ocorrer no mesmo dia até 22:00.
4. Conflitos são verificados para a unidade física, não apenas para o modelo do equipamento.

**Aceitação:** 09:30–12:30 é válido; 09:15–10:15 e 18:30–22:30 são inválidos.

### RN-15 — Antecedência máxima e fuso institucional

1. Uma reserva de laboratório ou equipamento poderá iniciar, no máximo, **336 horas exatas** (14 × 24 horas) após o instante atual do servidor.
2. O limite aplica-se ao **início previsto** da reserva; o instante exatamente no limite é permitido, desde que todos os demais requisitos sejam cumpridos.
3. O fuso institucional será **`America/Sao_Paulo`** para calendário local, horários de funcionamento e apresentação.
4. O backend deverá tratar instantes de forma inequívoca e manter o relógio servidor como referência; a representação técnica no banco e na API será definida na modelagem.

**Aceitação:** às 10:00 de 04/11, início às 10:00 de 18/11 é elegível quanto à antecedência máxima; 18/11 às 10:01 não é.

### RN-16 — Cotas por usuário

1. Para equipamentos, cada estudante ou professor pode manter no máximo **3 compromissos contáveis**, independentemente de sobreposições entre equipamentos distintos.
2. Contam: reservas `CONFIRMADA` futuras ou ainda dentro da janela válida de retirada; equipamentos `EMPRESTADA`, inclusive atrasados.
3. Não contam: reservas `EM_ANALISE`, `DEVOLVIDA`, `CANCELADA` ou `ENCERRADA`.
4. A passagem da reserva de `CONFIRMADA` para `EMPRESTADA` não pode contar como dois compromissos.
5. Antes de verificar cota, o sistema deve resolver ou reconhecer reservas `CONFIRMADA` cujo prazo de retirada já terminou, conforme RN-11; registro atrasado não deve manter cota artificialmente ocupada.
6. Professores podem manter até **5 reservas de laboratórios com início futuro** no estado `CONFIRMADA`, em cota separada da de equipamentos.
7. Cada reserva de laboratório de 45 ou 90 minutos conta como **um** compromisso; após chegar seu início previsto, deixa de ocupar a cota de futuras, independentemente de permanecer `CONFIRMADA` no histórico.
8. A cota deve ser respeitada também em solicitações concorrentes.

**Aceitação:** às 14:08, uma reserva de equipamento confirmada para 14:00 continua contando; um equipamento em análise não conta; professor com 5 reservas futuras de laboratórios não cria a sexta até a cota diminuir.

### RN-17 — Equipamentos diferentes no mesmo horário

1. O mesmo usuário poderá reservar equipamentos físicos **diferentes** em períodos total ou parcialmente sobrepostos.
2. Cada reserva ocupa uma vaga de sua cota, respeitando RN-16.
3. Não é permitido reservar a mesma unidade física para intervalos conflitantes, ainda que os titulares sejam diferentes.
4. Reservas permanecem independentes: cancelamento, retirada ou devolução de uma não altera automaticamente as outras.

**Aceitação:** câmera, tripé e microfone reservados das 14:00 às 16:00 pelo mesmo estudante são compatíveis entre si; quarto compromisso excede a cota.

### RN-18 — Identificação individual das unidades físicas

1. Cada unidade de equipamento terá cadastro individual e identificação única, mesmo quando houver vários aparelhos do mesmo modelo.
2. Reserva, retirada, devolução, histórico e manutenção se referem à unidade exata.
3. Apenas administrador cadastra, atualiza e desativa recursos.
4. Recurso desativado não aceita novas reservas; o histórico permanece preservado.
5. O MVP não usa controle de reserva apenas por quantidade agregada de modelo.

**Aceitação:** duas câmeras idênticas têm identificações e históricos diferentes; desativação impede novas reservas daquela unidade; não apaga reservas antigas.

### RN-19 — Manutenção por tempo indeterminado

1. Administrador poderá colocar equipamento específico em manutenção por prazo **indeterminado**, informando motivo e registrando responsável e início.
2. Até sua **liberação manual administrativa**, o equipamento não aceita novas reservas nem novas retiradas físicas.
3. Manutenção de uma unidade não bloqueia automaticamente outras unidades iguais.
4. Reservas previamente confirmadas não serão canceladas automaticamente; serão identificadas para análise e tratamento administrativo quando afetadas.
5. O registro de manutenção não impede devolução de empréstimo físico aberto.
6. Ao liberar, o administrador registra encerramento e responsável; o equipamento torna-se elegível para novos agendamentos apenas se as demais condições forem satisfeitas.
7. Liberação não restaura reservas anteriormente encerradas.
8. Histórico de períodos de manutenção deve ser preservado.

**Aceitação:** câmera em manutenção não admite nova reserva; outra unidade do mesmo modelo admite; devolução de câmera em manutenção continua possível; liberação só ocorre por administrador.

### RN-20 — Retificação administrativa limitada

1. Somente administrador pode retificar dados permitidos, sempre com **justificativa obrigatória**.
2. Podem ser corrigidos: observações administrativas, horários efetivos de retirada e devolução (sujeitos a validação) e motivos administrativos compatíveis de encerramento, sem mudar o estado final.
3. Cada retificação preserva campo, valor antigo e novo, instante da alteração, identidade do administrador e justificativa; retificações sucessivas preservam a sequência histórica.
4. Não é permitido alterar diretamente titular, equipamento ou período originalmente reservado.
5. A retificação não apaga retirada/devolução, não desfaz transições de estado e não reativa reservas encerradas.
6. Correções temporalmente impossíveis ou que violem integridade são rejeitadas; por exemplo, devolução anterior à retirada.
7. Correções não devem reabrir ou reclassificar automaticamente outras reservas já encerradas. Situações históricas impactadas exigem tratamento administrativo separado.
8. Alterações concorrentes não podem eliminar o histórico de retificações.

**Aceitação:** administrador corrige 16:30 para 16:10, com justificativa, mantendo registro original; tentativa de apagar uma devolução ou mudar a unidade física é rejeitada.

## 3. Máquina de estados aprovada

### 3.1 Laboratórios

Estados persistidos: `CONFIRMADA` e `CANCELADA`.

- Criação válida → `CONFIRMADA`.
- `CONFIRMADA` → `CANCELADA`, por cancelamento comum válido ou cancelamento administrativo excepcional autorizado.
- Passagem do horário não transforma uma reserva em `CONCLUIDA`: o histórico permanece `CONFIRMADA` se não foi cancelada.

### 3.2 Equipamentos

Estados persistidos: `CONFIRMADA`, `EMPRESTADA`, `DEVOLVIDA`, `CANCELADA`, `EM_ANALISE`, `ENCERRADA`.

| Origem | Destino | Condição |
|---|---|---|
| Criação | `CONFIRMADA` | Todas as validações de reserva |
| `CONFIRMADA` | `EMPRESTADA` | Retirada administrativa válida, durante a tolerância e sem bloqueios |
| `EMPRESTADA` | `DEVOLVIDA` | Devolução registrada por administrador |
| `CONFIRMADA` | `CANCELADA` | Cancelamento pelo titular dentro da regra ou excepcional administrativo |
| `CONFIRMADA` | `ENCERRADA` | Expiração sem impedimento, motivo `NAO_RETIRADA` |
| `CONFIRMADA` | `EM_ANALISE` | Sem retirada e com impedimento operacional registrado na janela |
| `CONFIRMADA` | `ENCERRADA` | Administrador comprova indisponibilidade e encerra, motivo `NAO_ATENDIDA` |
| `EM_ANALISE` | `ENCERRADA` | Decisão administrativa, motivo `NAO_ATENDIDA` ou `ANALISE_INCONCLUSIVA` |

- Estados finais de equipamento: `DEVOLVIDA`, `CANCELADA` e `ENCERRADA`.
- `ENCERRADA` exige motivo dentre `NAO_RETIRADA`, `NAO_ATENDIDA`, `ANALISE_INCONCLUSIVA`.
- Atraso é uma **condição calculada** de empréstimo `EMPRESTADA`, não um estado adicional.
- `EM_ANALISE` não conta na cota e não bloqueia calendário, mas preserva histórico e impede retirada comum.
- Não há reativação pelo fluxo comum.

## 4. Matriz conceitual de disponibilidade

| Condição | Bloqueia o intervalo originalmente reservado? | Restringe novas reservas? |
|---|---|---|
| Reserva `CONFIRMADA` ainda válida | Sim | Sim, para intervalos conflitantes |
| Reserva `EMPRESTADA` | Sim, quanto ao período reservado | Sim, se houver conflito; empréstimo ainda aberto determina disponibilidade física |
| Reserva `CANCELADA` / `ENCERRADA` | Não | Não por essa reserva isoladamente |
| Reserva `EM_ANALISE` | Não | Não por essa reserva isoladamente |
| Equipamento em manutenção | Independente da reserva | Sim, enquanto manutenção ativa |
| Equipamento desativado | Independente da reserva | Sim, para novas reservas |
| Empréstimo anterior atrasado e ainda aberto | Independente da reserva afetada | Sim, novas reservas futuras desse equipamento ficam bloqueadas até devolução |

**Atenção:** disponibilidade de horário não equivale à possibilidade física de retirada. Reservas já confirmadas não são canceladas automaticamente em razão de atraso de outro usuário.

## 5. Verificação final de consistência

### 5.1 Cruzamentos consistentes após as seis decisões finais

1. **RN-04 + RN-10:** a tolerância de cinco minutos após confirmação agora é aplicável inclusive à reserva de equipamento criada exatamente 30 minutos antes do início; jamais ultrapassa o início previsto.
2. **RN-11 + RN-13 + RN-19:** a verificação de impedimentos abrange toda a janela de retirada, não só o instante final. Impedimento conhecido não vira automaticamente `NAO_RETIRADA`.
3. **RN-13 + RN-16:** `EM_ANALISE` não consome a cota e não implica retirada física.
4. **RN-13 + RN-17 + RN-19:** liberar o bloqueio de calendário de uma reserva em análise não libera equipamento ainda emprestado, em manutenção ou desativado.
5. **RN-11 + RN-16:** `CONFIRMADA` conta na cota durante os 15 minutos; após o prazo, precisa ser tratada conforme expiração/análise antes da nova contagem.
6. **RN-12 + RN-16:** empréstimo atrasado continua contando na cota e impede novas reservas/retiradas de equipamentos do seu titular, mas não cancela agendamentos já confirmados.
7. **RN-15 + RN-02/RN-09:** 336 horas é teto do início previsto; grades acadêmicas e funcionamento continuam prevalecendo.
8. **RN-05 + RN-07 + RN-20:** somente administrador realiza movimentações e retificações; cadastro público não cria contas privilegiadas.
9. **RN-13 + RN-20:** retificar um motivo histórico não reativa a reserva nem reexecuta consequências sobre outras reservas.
10. **RN-18 + RN-19:** manutenção e disponibilidade pertencem à unidade física específica, não ao modelo agregado.

### 5.2 Limites de escopo e detalhes ainda não aprovados

Estas lacunas **não alteram as seis decisões finais** e não exigem inventar novas RN agora; porém precisam de encaminhamento antes de implementar os respectivos casos:

- **Recursos desativados com reservas futuras:** não foi aprovado um rito completo de tratamento dessas reservas. Manutenção já tem política própria, mas desativação definitiva exige cuidado semelhante.
- **Cancelamento administrativo de laboratório após o começo do período:** foi aprovada a exceção após o limite comum, mas não definido expressamente até que instante ela pode ser usada; evitar tratar silenciosamente uma reserva histórica como cancelada.
- **Registros de movimentações efetivas fora do horário planejado ou lançados posteriormente:** RN-09 limita horários **previstos** e RN-20 permite retificação, mas falta delimitar a operação física fora de expediente e como registrar retrospectivamente sem falsear a tolerância de retirada.
- **Reservas consecutivas sem intervalo operacional:** horários encostados são temporalmente não sobrepostos, mas não há decisão específica sobre exigir tempo de conferência física entre empréstimos.
- **Detalhes de implementação, não de produto:** esquema de banco, estratégia transacional/locks, processamento de vencimentos, autenticação por sessão ou token, formato de erros, logs e representação de instantes/zonas no PostgreSQL ainda serão definidos e validados na etapa técnica.

### 5.3 Veredito

As **seis pendências finais escolhidas foram encerradas**, e as **RN-01 a RN-20 podem ser usadas como linha de base funcional do MVP acadêmico**. A verificação textual encontra pontos de borda operacionais que devem permanecer explicitamente rastreados, sem presumir aprovação. Portanto, a consolidação documental está concluída, mas a homologação de todos os cenários excepcionais e a validação por testes ainda não ocorreram.

## 6. Testes de aceitação prioritários para a futura implementação

1. Disputa concorrente pela mesma unidade e mesmo intervalo: nunca confirmar duas reservas conflitantes.
2. Duas requisições concorrentes para o mesmo usuário: nunca ultrapassar a cota.
3. Professor reserva bloco de 90 minutos: ambos os períodos precisam estar livres e pertencer ao mesmo bloco.
4. Cadastro público: nunca cria conta administrativa ou docente por autoatribuição.
5. Equipamento confirmado exatamente 30 minutos antes do início: prazo excepcional de cancelamento de 5 minutos aplicado corretamente.
6. Retirada exatamente no minuto +15: permitida; após +15: rejeitada.
7. Impedimento encerrado durante a janela: encaminhamento a `EM_ANALISE` em vez de `NAO_RETIRADA`.
8. `EM_ANALISE`: não ocupa cota nem calendário; manutenção e empréstimos físicos ainda restringem disponibilidade.
9. Empréstimo atrasado: bloqueia novas reservas e retiradas do titular; não cancela automaticamente reservas preexistentes.
10. Equipamento atrasado: rejeita novas reservas futuras para a mesma unidade até sua devolução.
11. Encerramentos `NAO_ATENDIDA` e `ANALISE_INCONCLUSIVA`: justificativa e administrador obrigatórios, sem reativação.
12. Retificação: preserva antes/depois, justificativa e autor, impede devolução anterior à retirada e não desfaz estados.

## 7. Estado do trabalho

- **Aprovado:** decisões de negócio RN-01 a RN-20 e as seis últimas políticas de fechamento.
- **Consolidado:** esta especificação documental de trabalho.
- **Não feito:** alterações em código, execução de testes, verificação de versões de dependências ou implantação.
- **Próxima atividade recomendada:** revisar a arquitetura e o roadmap em etapa própria, tomando esta especificação e `docs/requisitos.md` como referências funcionais; definir os limites operacionais pendentes antes de implementar os respectivos casos.