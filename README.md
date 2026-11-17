# 🐾 PetCare Desktop

Sistema desktop para gestão de pet shop: cadastro de clientes, pets e serviços, e agenda de atendimentos.

!\[Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
!\[Java](https://img.shields.io/badge/Java-11%2B-orange)
!\[MySQL](https://img.shields.io/badge/MySQL-8-blue)

## Status do projeto

🚧 **Em desenvolvimento** — Projeto Integrador (desktop), etapas 1 a 5.

## Tecnologias

Apenas tecnologias abordadas no curso:

|Tecnologia|Uso no projeto|
|-|-|
|**Java** (11+)|Linguagem de programação, orientada a objetos|
|**NetBeans**|IDE de desenvolvimento (projeto Maven)|
|**Java Swing**|Interface gráfica desktop|
|**MySQL** e **MySQL Workbench**|Banco de dados e modelagem/execução dos scripts|
|**JDBC**|Conexão entre o Java e o MySQL|
|**UML**|Diagrama de classes|
|**Figma**|Wireframes e projeto de interfaces (UX/UI)|
|**Git** e **GitHub**|Versionamento e repositório remoto|

## Time de desenvolvedores

|Nome|Função|
|-|-|
|Washington Conceição Costa|Desenvolvedor (análise, interface, programação e banco de dados)|

## Objetivo do software

Substituir cadernos e planilhas no controle de um pequeno pet shop, permitindo à equipe de atendimento **cadastrar clientes e seus animais, manter o catálogo de serviços e agendar atendimentos sem conflito de horários**, além de acompanhar rapidamente o resumo do dia (atendimentos e receita prevista).

## Funcionalidades do sistema (requisitos)

### Requisitos funcionais

|Código|Funcionalidade|
|-|-|
|RF01|Cadastrar, consultar, alterar e excluir **clientes** (nome, CPF, telefone, e-mail)|
|RF02|Cadastrar, consultar, alterar e excluir **pets** (nome, espécie, raça, porte, tutor)|
|RF03|Cadastrar, consultar, alterar e excluir **serviços** (nome, descrição, preço, duração)|
|RF04|Buscar registros por texto em todas as listas|
|RF05|**Agendar atendimento** (pet, serviço, data e horário) com verificação de conflito|
|RF06|Concluir ou cancelar agendamentos|
|RF07|Painel inicial com indicadores do dia e próximos atendimentos|

### Regras de negócio

* CPF válido e único por cliente.
* Todo pet pertence a um cliente.
* Não pode haver dois atendimentos agendados no mesmo horário (expediente das 08:00 às 17:30, de 30 em 30 minutos).
* Só é possível concluir/cancelar agendamentos com status *Agendado*.
* Não é possível excluir cliente com pets, nem pet ou serviço com agendamentos.

### Requisitos não funcionais

Usabilidade e acessibilidade (contraste mínimo 4,5:1, navegação por teclado), persistência em MySQL, consultas parametrizadas e código organizado em camadas.

## Como executar

1. **Banco de dados:** no MySQL Workbench, abra e execute o script [`sql/petcare.sql`](sql/petcare.sql).
2. **Configuração:** em `src/main/resources/db.properties`, informe o usuário e a **senha** do seu MySQL.
3. **Projeto:** no NetBeans, use *File > Open Project* e selecione a pasta do projeto (o Maven baixa o driver JDBC sozinho).
4. **Execução:** clique com o botão direito no projeto > *Run* (classe principal `br.com.petcare.Main`).

## Estrutura do repositório

```
petcare-desktop/
├── pom.xml
├── sql/petcare.sql                 # criação do banco e dados de teste
├── src/main/java/br/com/petcare/
│   ├── Main.java
│   ├── model/                      # classes do domínio
│   ├── visao/                      # telas Swing
│   ├── servico/                    # regras de negócio
│   ├── repositorio/                # contratos de persistência
│   ├── dao/                        # acesso ao MySQL (JDBC)
│   ├── util/  excecao/
├── src/main/resources/db.properties
└── docs/
    ├── documento\_de\_projeto\_petcare.pdf
    ├── uml/                        # diagrama de classes
    ├── ux-ui/                      # wireframes, telas e paleta
    └── telas-sistema/              # capturas do sistema
```

## Documentação

* [Documento de projeto](docs/documento_de_projeto_petcare.pdf)
* [Projeto de UX/UI](docs/ux-ui/projeto_ux_ui_petcare.pdf)
* [Diagrama de classes](docs/uml/diagrama_uml_petcare.png)

## Telas

!\[Início](docs/telas-sistema/inicio.png)
!\[Agendamentos](docs/telas-sistema/agendamentos.png)





Projeto desenvolvido para a disciplina de Projeto Integrador.

