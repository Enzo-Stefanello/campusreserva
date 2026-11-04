# Roadmap do CampusReserva

O desenvolvimento será incremental. Uma etapa só será marcada como concluída quando seus critérios forem verificados.

## Fase 0 — Fundação (em andamento)

- [x] Definir visão e posicionamento do produto.
- [x] Criar documentos iniciais do projeto.
- [ ] Revisar e aprovar as regras do MVP.
- [ ] Confirmar ferramentas e versões para o ambiente de desenvolvimento.

**Entrega:** requisitos e arquitetura iniciais revisados, backlog priorizado.

## Fase 1 — Base técnica

- [ ] Criar aplicação Spring Boot.
- [ ] Configurar PostgreSQL no ambiente local.
- [ ] Configurar migrações com Flyway.
- [ ] Criar endpoint de verificação de saúde.
- [ ] Adicionar instruções de execução.

**Entrega:** backend inicial executando localmente e conectado ao banco.

## Fase 2 — Recursos

- [ ] Modelar entidade de recurso.
- [ ] Implementar cadastro e listagem de recursos.
- [ ] Validar entradas e padronizar respostas de erro.
- [ ] Criar testes da funcionalidade.

**Entrega:** recursos persistidos e consultáveis pela API.

## Fase 3 — Reservas

- [ ] Modelar entidade de reserva.
- [ ] Implementar criação e consulta de reservas.
- [ ] Validar períodos e impedir sobreposição.
- [ ] Testar concorrência entre solicitações simultâneas.
- [ ] Implementar cancelamento conforme regras aprovadas.

**Entrega:** fluxo de reserva com integridade dos dados verificada.

## Fase 4 — Segurança

- [ ] Implementar autenticação.
- [ ] Definir e aplicar permissões de usuário e administrador.
- [ ] Testar tentativas de acesso não autorizado.

**Entrega:** operações protegidas conforme o perfil do usuário.

## Fase 5 — Interface

- [ ] Inicializar React + TypeScript.
- [ ] Criar telas de listagem e disponibilidade.
- [ ] Implementar fluxo de reserva e histórico.
- [ ] Integrar estados de carregamento, sucesso e erro.

**Entrega:** fluxo principal utilizável no navegador.

## Fase 6 — Entrega de portfólio

- [ ] Consolidar testes e CI.
- [ ] Revisar segurança e configuração de ambientes.
- [ ] Publicar instruções reproduzíveis e capturas de tela reais.
- [ ] Avaliar e implementar deploy demonstrável.
- [ ] Revisar documentação técnica e apresentação profissional.

**Entrega:** versão demonstrável, testada e documentada.
