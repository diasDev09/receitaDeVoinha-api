package com.receitadevoinha.api.exception;

/** 401: sem sessao valida ou credenciais erradas. */
public class NaoAutenticadoException extends RuntimeException {

    public NaoAutenticadoException(String mensagem) {
        super(mensagem);
    }
}
