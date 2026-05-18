-- 1. Criando a tabela Pessoa
CREATE TABLE Pessoa (
    idPessoa SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    cep CHAR(9),
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL
);

-- 2. Criando a tabela Livro
CREATE TABLE Livro (
    idLivro SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    data_lancamento DATE
);

-- 3. Criando a tabela Emprestimo (tabela de ligação)
CREATE TABLE Emprestimo (
    idLivro INT NOT NULL,
    idPessoa INT NOT NULL,
    PRIMARY KEY (idLivro, idPessoa),
    CONSTRAINT fk_livro FOREIGN KEY (idLivro) REFERENCES Livro(idLivro) ON DELETE CASCADE,
    CONSTRAINT fk_pessoa FOREIGN KEY (idPessoa) REFERENCES Pessoa(idPessoa) ON DELETE CASCADE
);