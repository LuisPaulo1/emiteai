package com.emiteai.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RecursoNaoEncontradoException extends RuntimeException {
        public RecursoNaoEncontradoException(String mensagem) {
            super(mensagem);
        }
}