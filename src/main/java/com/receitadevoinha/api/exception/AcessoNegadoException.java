package com.receitadevoinha.api.exception;

/** 403: usuario autenticado, mas sem permissao para a operacao. */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
