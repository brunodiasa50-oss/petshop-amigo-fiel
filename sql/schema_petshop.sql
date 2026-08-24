-- =====================================================================
-- Petshop Amigo Fiel - Script de criacao do banco de dados
-- Etapa 4 - Projeto Integrador
--
-- Como usar (MySQL Workbench ou linha de comando):
--   1. Abra este script no MySQL Workbench (File > Open SQL Script).
--   2. Execute o script inteiro (icone de raio, ou Ctrl+Shift+Enter).
--      Isso cria o banco "db_petshop", as tabelas e alguns dados de
--      exemplo para teste.
--
-- Usuario/senha esperados pela aplicacao (ver ConexaoBD.java):
--   Host: localhost   Porta: 3306
--   Banco: db_petshop
--   Usuario: petshop   Senha: petshop123
--
-- Caso prefira usar outro usuario (ex.: root), ajuste as constantes
-- USUARIO e SENHA em src/petshop/util/ConexaoBD.java.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS db_petshop
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Cria o usuario dedicado da aplicacao (ignorado se ja existir)
CREATE USER IF NOT EXISTS 'petshop'@'localhost' IDENTIFIED BY 'petshop123';
GRANT ALL PRIVILEGES ON db_petshop.* TO 'petshop'@'localhost';
FLUSH PRIVILEGES;

USE db_petshop;

-- ---------------------------------------------------------------------
-- Tabela: cliente
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS agendamento;
DROP TABLE IF EXISTS animal;
DROP TABLE IF EXISTS cliente;

CREATE TABLE cliente (
    idCliente   INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(120) NOT NULL,
    telefone    VARCHAR(30)  NOT NULL,
    email       VARCHAR(120)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabela: animal (vinculado a um cliente)
-- ---------------------------------------------------------------------
CREATE TABLE animal (
    idAnimal    INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(80) NOT NULL,
    especie     VARCHAR(40) NOT NULL,
    raca        VARCHAR(80),
    idCliente   INT NOT NULL,
    CONSTRAINT fk_animal_cliente
        FOREIGN KEY (idCliente) REFERENCES cliente(idCliente)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabela: agendamento (vinculado a um animal)
-- ---------------------------------------------------------------------
CREATE TABLE agendamento (
    idAgendamento INT AUTO_INCREMENT PRIMARY KEY,
    idAnimal      INT NOT NULL,
    servico       VARCHAR(60) NOT NULL,
    dataHora      DATETIME NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'Agendado',
    CONSTRAINT fk_agendamento_animal
        FOREIGN KEY (idAnimal) REFERENCES animal(idAnimal)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Dados iniciais para teste
-- ---------------------------------------------------------------------
INSERT INTO cliente (nome, telefone, email) VALUES
    ('Joao Silva',  '(21) 99999-0000', 'joao@email.com'),
    ('Maria Souza', '(21) 98888-7777', 'maria@email.com'),
    ('Ana Lima',    '(21) 97777-1234', 'ana.lima@email.com');

INSERT INTO animal (nome, especie, raca, idCliente) VALUES
    ('Rex',  'Cachorro', 'Vira-lata', 1),
    ('Mia',  'Gato',     'Siames',    2),
    ('Thor', 'Cachorro', 'Labrador',  3);

INSERT INTO agendamento (idAnimal, servico, dataHora, status) VALUES
    (1, 'Banho e Tosa', DATE_ADD(NOW(), INTERVAL 2 HOUR), 'Agendado'),
    (2, 'Consulta',     DATE_ADD(NOW(), INTERVAL 4 HOUR), 'Agendado'),
    (3, 'Banho',        DATE_SUB(NOW(), INTERVAL 1 DAY),  'Concluido');
