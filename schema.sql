CREATE TABLE IF NOT EXISTS usuario (
    id_usuario int NOT NULL AUTO_INCREMENT,
    nome       varchar(255) NOT NULL,
    email      varchar(100) NOT NULL UNIQUE,
    senha      varchar(255) NOT NULL,
    perfil     varchar(255) NOT NULL DEFAULT 'FUNCIONARIO',
    ativo      boolean NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_usuario)
    );

CREATE TABLE IF NOT EXISTS cliente (
    id_cliente int NOT NULL AUTO_INCREMENT,
    nome varchar(255) DEFAULT NULL,
    endereco varchar(255) NOT NULL,
    cpf varchar(255) NOT NULL,
    telefone varchar(255) DEFAULT NULL,
    bairro varchar(255) DEFAULT NULL,
    PRIMARY KEY (id_cliente)
);

CREATE TABLE IF NOT EXISTS adicional (
    id_adicional int NOT NULL AUTO_INCREMENT,
    nome varchar(255) NOT NULL,
    valor double NOT NULL,
    quantidade int NOT NULL,
    PRIMARY KEY (id_adicional)
);

CREATE TABLE IF NOT EXISTS pizzas (
    id_pizza int NOT NULL AUTO_INCREMENT,
    tipo varchar(255) NOT NULL,
    valor double NOT NULL,
    PRIMARY KEY (id_pizza)
);

-- TABELA PRINCIPAL DO PEDIDO (Contém os dados que o seu PedidoDAO precisa buscar e atualizar)
CREATE TABLE IF NOT EXISTS pedido (
    id_pedido int NOT NULL AUTO_INCREMENT,
    id_cliente int DEFAULT NULL,
    id_pizza int DEFAULT NULL,
    forma_pagamento varchar(255) NOT NULL,
    observacao varchar(255) DEFAULT NULL,
    tamanho varchar(255) NOT NULL,
    data_pedido DATE NOT NULL,
    estado varchar(50) DEFAULT 'EM_ANDAMENTO', -- Campo que vai receber o "FINALIZADO"
    PRIMARY KEY (id_pedido),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente),
    CONSTRAINT fk_pedido_pizza FOREIGN KEY (id_pizza) REFERENCES pizzas (id_pizza)
);

-- TABELA DE LIGAÇÃO (Muitos para Muitos entre Pedido e Adicional)
CREATE TABLE IF NOT EXISTS pedido_adicional (
    id_pedido int NOT NULL,
    id_adicional int NOT NULL,
    PRIMARY KEY (id_pedido, id_adicional),
    CONSTRAINT fk_ligacao_pedido FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido),
    CONSTRAINT fk_ligacao_adicional FOREIGN KEY (id_adicional) REFERENCES adicional (id_adicional)
);

CREATE TABLE IF NOT EXISTS reposicao_estoque (
    id_reposicao INT NOT NULL AUTO_INCREMENT,
    id_adicional INT NOT NULL,
    quantidade INT NOT NULL,
    valor_unitario DOUBLE NOT NULL,
    valor_total DOUBLE NOT NULL,
    data_reposicao DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_reposicao),
    CONSTRAINT fk_reposicao_adicional FOREIGN KEY (id_adicional) REFERENCES adicional (id_adicional)
);