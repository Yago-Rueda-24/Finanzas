package com.YagoRueda.Finanzas.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class InputTransactionException extends Exception {

    @Getter
    @Setter
    private Map<String, String> errors;


    public InputTransactionException(String message) {
        super(message);
        errors = new HashMap<>();
    }
}
