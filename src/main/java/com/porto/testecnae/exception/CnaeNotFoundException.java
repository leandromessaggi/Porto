package com.porto.testecnae.exception;

public class CnaeNotFoundException extends RuntimeException {

    public CnaeNotFoundException(String codigo) {
        super("CNAE não encontrado para o código: " + codigo);
    }
}