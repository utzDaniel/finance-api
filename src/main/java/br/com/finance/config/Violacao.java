package br.com.finance.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.springframework.validation.FieldError;

public record Violacao(
        String campo,
        String razao
) {
    public Violacao(FieldError fieldError) {
        this(fieldError.getField(), fieldError.getDefaultMessage());
    }

    public Violacao(ConstraintViolation<?> violation) {
        this(getCampo(violation.getPropertyPath()), violation.getMessage());
    }

    private static String getCampo(Path path) {
        return path.toString().substring(path.toString().lastIndexOf(".") + 1);
    }

}

