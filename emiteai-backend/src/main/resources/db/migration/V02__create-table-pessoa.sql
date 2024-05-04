CREATE TABLE pessoa
(
    id               INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    nome             VARCHAR(100)                             NOT NULL,
    telefone         VARCHAR(30)                              NOT NULL,
    cpf              VARCHAR(14)                             NOT NULL,
    endereco_id      INTEGER                                  NOT NULL,
    data_criacao     TIMESTAMP WITHOUT TIME ZONE,
    data_atualizacao TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_pessoa PRIMARY KEY (id)
);

ALTER TABLE pessoa
    ADD CONSTRAINT uc_pessoa_cpf UNIQUE (cpf);

ALTER TABLE pessoa
    ADD CONSTRAINT FK_PESSOA_ENDERECO FOREIGN KEY (endereco_id) REFERENCES endereco (id);