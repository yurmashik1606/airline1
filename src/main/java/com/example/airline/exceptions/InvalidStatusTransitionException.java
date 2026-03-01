package com.example.airline.exceptions;

/**
 * Исключение, выбрасываемое при попытке недопустимого перехода статуса рейса.
 *
 * @author Студент группы _______
 * @version 1.0
 */
public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
