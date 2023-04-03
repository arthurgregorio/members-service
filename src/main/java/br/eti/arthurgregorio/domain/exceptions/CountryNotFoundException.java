package br.eti.arthurgregorio.domain.exceptions;

public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException(String message) {
        super(message);
    }
}
