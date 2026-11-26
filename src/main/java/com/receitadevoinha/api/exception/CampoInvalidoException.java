package com.receitadevoinha.api.exception;

/** 400 ligado a um campo (ex.: categoria que nao existe), devolvido em "fields". */
public class CampoInvalidoException extends RuntimeException {

    private final String campo;

    public CampoInvalidoException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
