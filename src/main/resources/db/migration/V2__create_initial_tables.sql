-- 1. Criando a tabela pessoa
CREATE TABLE pessoa (
    id_pessoa SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    cep CHAR(9),
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL
);

-- 2. Criando a tabela livro
CREATE TABLE livro (
    id_livro SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    data_lancamento DATE
);

-- 3. Criando a tabela emprestimo (tabela de ligação)
CREATE TABLE emprestimo (
    id_livro INT NOT NULL,
    id_pessoa INT NOT NULL,
    PRIMARY KEY (id_livro, id_pessoa),
    CONSTRAINT fk_livro FOREIGN KEY (id_livro) REFERENCES livro(id_livro) ON DELETE CASCADE,
    CONSTRAINT fk_pessoa FOREIGN KEY (id_pessoa) REFERENCES pessoa(id_pessoa) ON DELETE CASCADE
);

ALTER TABLE livro ADD COLUMN quantidade INT NOT NULL DEFAULT 1;