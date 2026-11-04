# Requisitos do CampusReserva

> Documento inicial de planejamento. As decisões abaixo são propostas para discussão, não funcionalidades já existentes.

## 1. Objetivo

Permitir que uma instituição de ensino administre recursos reserváveis (salas, laboratórios e equipamentos) e que usuários consultem disponibilidade e solicitem reservas sem conflitos de horário.

## 2. Perfis de usuário propostos

- **Usuário autenticado:** consulta recursos, solicita reservas, consulta seu histórico e solicita cancelamentos.
- **Administrador:** cadastra/atualiza recursos e acompanha as reservas do sistema.

A necessidade de um perfil intermediário, como docente ou responsável por laboratório, será avaliada posteriormente.

## 3. Requisitos funcionais (propostos para o MVP)

| ID | Requisito | Critério inicial de aceitação |
| --- | --- | --- |
| RF-01 | Consultar recursos | O sistema apresenta uma lista paginada ou filtrável de recursos disponíveis para reserva. |
| RF-02 | Gerenciar recursos | Um administrador pode cadastrar e desativar recursos, com validação de dados. |
| RF-03 | Consultar disponibilidade | O usuário informa recurso e intervalo; o servidor verifica conflitos com reservas existentes. |
| RF-04 | Criar reserva | Uma reserva válida é persistida e associada ao usuário e ao recurso. |
| RF-05 | Impedir sobreposição | Duas reservas ativas de um mesmo recurso não podem ocupar horários que se sobrepõem. |
| RF-06 | Consultar minhas reservas | O usuário autenticado visualiza suas reservas e os respectivos estados. |
| RF-07 | Cancelar reserva | O sistema aplica as regras de cancelamento definidas para o MVP. |
| RF-08 | Autenticar usuários | Operações protegidas exigem autenticação. |
| RF-09 | Separar permissões | Ações administrativas não ficam disponíveis a usuários comuns. |

## 4. Regras de negócio iniciais

- Cada reserva referencia exatamente um recurso e um usuário.
- O intervalo da reserva deve ter início estritamente anterior ao término.
- Reservas de um mesmo recurso não podem se sobrepor quando estiverem ativas.
- Consultas de disponibilidade não substituem a validação no momento da gravação.
- Recursos desativados não podem receber novas reservas.
- Erros de validação e de conflito devem produzir respostas compreensíveis para o cliente.

**A confirmar antes de implementar:** horários de funcionamento, duração mínima/máxima, antecedência das reservas, fuso horário, aprovação administrativa, política de cancelamento, limites por usuário e regras para reservas recorrentes.

## 5. Requisitos não funcionais

- Backend organizado com responsabilidades claras e testes automatizados para regras críticas.
- Persistência relacional com migrações versionadas.
- Tratamento de concorrência para evitar dupla reserva.
- Dados sensíveis e segredos fora do repositório.
- Documentação dos endpoints e instruções reproduzíveis de execução.
- Interface responsiva e mensagens claras de erro e sucesso.

## 6. Fora do primeiro MVP

- Integração com calendário institucional.
- Notificações por e-mail e push.
- Pagamentos ou cobrança pelo uso dos recursos.
- Integração com sistemas acadêmicos de terceiros.
- Arquitetura de microsserviços.

Essas funcionalidades podem ser reavaliadas apenas depois de uma versão básica funcionando e testada.
