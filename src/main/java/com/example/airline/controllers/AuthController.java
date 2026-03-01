package com.example.airline.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер аутентификации.
 * Обрабатывает отображение страницы входа в систему.
 *
 * @author Студент группы ДЦПУП23-1
 * @version 1.0
 */
@Controller
public class AuthController {

    /**
     * Отображает страницу входа в систему.
     *
     * @param error   параметр, указывающий на ошибку аутентификации
     * @param logout  параметр, указывающий на успешный выход
     * @param model   модель для передачи данных в шаблон
     * @return имя шаблона auth/login
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                             @RequestParam(required = false) String logout,
                             Model model) {
        if (error != null) model.addAttribute("error", "Неверный логин или пароль.");
        if (logout != null) model.addAttribute("message", "Вы успешно вышли из системы.");
        return "auth/login";
    }
}
