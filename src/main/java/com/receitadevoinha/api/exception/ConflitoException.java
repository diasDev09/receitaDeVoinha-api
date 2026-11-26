package com.receitadevoinha.api.exception;

/** 409: o dado já existe (ex.: nome de usuário já cadastrado). Vai em "fields". */
public class ConflitoException extends RuntimeException {

    private final String campo;

    public ConflitoException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
