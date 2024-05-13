package com.emiteai.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NegocioException extends RuntimeException {
	public NegocioException(String mensagem) {
		super(mensagem);
	}
}