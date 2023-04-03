package br.eti.arthurgregorio.application.controllers.advice;

import br.eti.arthurgregorio.domain.exceptions.CountryNotFoundException;
import br.eti.arthurgregorio.domain.exceptions.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handle(MethodArgumentNotValidException ex) {

        final var errors = new HashMap<>();

        for (var error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        final var problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Some fields are invalid or missing the value"
        );

        problemDetail.setTitle("Unprocessable Payload");
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler({
            MemberNotFoundException.class,
            CountryNotFoundException.class
    })
    ProblemDetail handle(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
