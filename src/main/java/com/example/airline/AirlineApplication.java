package com.example.airline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс запуска приложения.
 * Информационно-справочная система учёта рейсов авиакомпании.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@SpringBootApplication
public class AirlineApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(AirlineApplication.class, args);
    }
}
