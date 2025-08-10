package com.example.bankcards.exception;

/**
 * Базовое пользовательское исключение для приложения.
 * От него наследуются все остальные бизнес-исключения.
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}