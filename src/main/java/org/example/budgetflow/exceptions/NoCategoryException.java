package org.example.budgetflow.exceptions;

public class NoCategoryException extends RuntimeException {
    public NoCategoryException(String message) {
        super(message);
    }
}
