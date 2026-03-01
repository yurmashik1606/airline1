package com.example.airline.exceptions;

/**
 * Исключение, выбрасываемое при конфликте назначения воздушного судна.
 * Возникает когда воздушное судно уже занято в указанный период.
 *
 * @author Студент группы _______
 * @version 1.0
 */
public class AircraftConflictException extends RuntimeException {
    public AircraftConflictException(String message) {
        super(message);
    }
}
