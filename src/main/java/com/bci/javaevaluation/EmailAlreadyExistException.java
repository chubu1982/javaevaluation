package com.bci.javaevaluation;

public class EmailAlreadyExistException extends Exception {
    public EmailAlreadyExistException(String email) {
        super(String.format("El correo %s ya fue registrado.", email));
    }
}
