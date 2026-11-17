-- =====================================================================
--  PetCare Desktop - Script do banco de dados (MySQL 8.x)
--  Como usar no MySQL Workbench:
--    1) Abra este arquivo (File > Open SQL Script)
--    2) Execute tudo (ícone do raio ou Ctrl+Shift+Enter)
--  ATENÇÃO: o script recria as tabelas; se já existirem dados, eles serão apagados.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS petcare
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE petcare;

DROP TABLE IF EXISTS agendamento;
DROP TABLE IF EXISTS pet;
DROP TABLE IF EXISTS servico;
DROP TABLE IF EXISTS cliente;

-- ---------------------------------------------------------------- cliente
CREATE TABLE cliente (
    id        INT          NOT NULL AUTO_INCREMENT,
    nome      VARCHAR(100) NOT NULL,
    cpf       CHAR(11)     NOT NULL,
    telefone  VARCHAR(11)  NOT NULL,
    email     VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_cliente_cpf (cpf)
) ENGINE = InnoDB;

-- -------------------------------------------------------------------- pet
CREATE TABLE pet (
    id          INT         NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(60) NOT NULL,
    especie     VARCHAR(30) NOT NULL,
    raca        VARCHAR(60) NULL,
    porte       ENUM('PEQUENO', 'MEDIO', 'GRANDE') NOT NULL,
    id_cliente  INT         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_pet_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------- servico
CREATE TABLE servico (
    id               INT            NOT NULL AUTO_INCREMENT,
    nome             VARCHAR(80)    NOT NULL,
    descricao        VARCHAR(255)   NULL,
    preco            DECIMAL(10, 2) NOT NULL,
    duracao_minutos  INT            NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_servico_nome (nome),
    CONSTRAINT ck_servico_preco CHECK (preco > 0),
    CONSTRAINT ck_servico_duracao CHECK (duracao_minutos > 0)
) ENGINE = InnoDB;

-- ------------------------------------------------------------ agendamento
CREATE TABLE agendamento (
    id           INT          NOT NULL AUTO_INCREMENT,
    id_pet       INT          NOT NULL,
    id_servico   INT          NOT NULL,
    data_hora    DATETIME     NOT NULL,
    status       ENUM('AGENDADO', 'CONCLUIDO', 'CANCELADO') NOT NULL DEFAULT 'AGENDADO',
    observacoes  VARCHAR(255) NULL,
    PRIMARY KEY (id),
    KEY idx_agendamento_data (data_hora),
    CONSTRAINT fk_agendamento_pet FOREIGN KEY (id_pet)
        REFERENCES pet (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_agendamento_servico FOREIGN KEY (id_servico)
        REFERENCES servico (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- =====================================================================
--  Dados iniciais para teste (datas relativas ao dia em que o script é executado)
-- =====================================================================
INSERT INTO cliente (nome, cpf, telefone, email) VALUES
    ('Mariana Souza',       '52998224725', '21987654321', 'mariana.souza@email.com'),
    ('Carlos Eduardo Lima', '11144477735', '21998877665', 'carlos.lima@email.com'),
    ('Fernanda Oliveira',   '39053344705', '2133445566',  NULL),
    ('Roberto Alves',       '93541134780', '21976543210', 'roberto.alves@email.com');

INSERT INTO pet (nome, especie, raca, porte, id_cliente) VALUES
    ('Thor', 'Cão',  'Golden Retriever', 'GRANDE',  1),
    ('Mel',  'Gato', 'Siamês',           'PEQUENO', 1),
    ('Bob',  'Cão',  'Poodle',           'PEQUENO', 2),
    ('Luna', 'Cão',  'Labrador',         'GRANDE',  3),
    ('Rex',  'Cão',  'Vira-lata',        'MEDIO',   4);

INSERT INTO servico (nome, descricao, preco, duracao_minutos) VALUES
    ('Banho',                 'Banho com shampoo hipoalergênico e secagem', 60.00, 60),
    ('Tosa higiênica',        'Tosa nas regiões íntimas, patas e rosto',    45.00, 30),
    ('Banho e tosa completa', 'Banho, tosa completa e hidratação',         110.00, 90),
    ('Corte de unhas',        'Corte e lixamento das unhas',                25.00, 30),
    ('Consulta veterinária',  'Avaliação clínica geral',                   150.00, 30);

INSERT INTO agendamento (id_pet, id_servico, data_hora, status, observacoes) VALUES
    (1, 1, TIMESTAMP(CURDATE(), '09:00:00'),                          'AGENDADO',  'Alérgico a perfume'),
    (3, 2, TIMESTAMP(CURDATE(), '10:30:00'),                          'AGENDADO',  NULL),
    (4, 3, TIMESTAMP(CURDATE(), '14:00:00'),                          'AGENDADO',  'Trazer carteira de vacinação'),
    (2, 4, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:30:00'),'AGENDADO',  NULL),
    (5, 5, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '11:00:00'),'AGENDADO',  'Retorno de vacina'),
    (1, 3, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '15:00:00'),'CONCLUIDO', NULL),
    (3, 1, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 3 DAY), '10:00:00'),'CONCLUIDO', NULL),
    (4, 2, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 5 DAY), '08:30:00'),'CANCELADO', 'Cliente remarcou');

-- Conferência rápida
SELECT 'cliente' AS tabela, COUNT(*) AS registros FROM cliente
UNION ALL SELECT 'pet', COUNT(*) FROM pet
UNION ALL SELECT 'servico', COUNT(*) FROM servico
UNION ALL SELECT 'agendamento', COUNT(*) FROM agendamento;
