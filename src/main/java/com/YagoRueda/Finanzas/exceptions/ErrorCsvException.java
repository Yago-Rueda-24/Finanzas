package com.YagoRueda.Finanzas.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ErrorCsvException extends RuntimeException {
    private List<Long> errors;

    public ErrorCsvException(String message) {
        super(message);
        errors = new ArrayList<>();
    }

    public List<Long> getErrors() {
        return errors;
    }

    public void setErrors(List<Long> errors) {
        this.errors = errors;
    }
}
