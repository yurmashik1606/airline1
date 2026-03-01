package com.example.airline.controllers;

import com.example.airline.services.FlightService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер главной страницы и просмотра рейсов.
 * Обрабатывает запросы диспетчеров к списку и деталям рейсов.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Controller
public class MainController {

    private final FlightService flightService;

    public MainController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Перенаправляет с корня сайта на страницу рейсов.
     *
     * @return редирект на /flights
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/flights";
    }

    /**
     * Отображает список всех рейсов с возможностью поиска.
     *
     * @param query  строка поиска (необязательный параметр)
     * @param model  модель для передачи данных в шаблон
     * @return имя шаблона flights/list
     */
    @GetMapping("/flights")
    public String flightsList(@RequestParam(required = false) String query, Model model) {
        if (query != null && !query.isBlank()) {
            model.addAttribute("flights", flightService.search(query));
            model.addAttribute("query", query);
        } else {
            model.addAttribute("flights", flightService.findAll());
        }
        return "flights/list";
    }

    /**
     * Отображает детальную страницу рейса с журналом событий.
     *
     * @param id    идентификатор рейса
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона flights/detail или редирект при ошибке
     */
    @GetMapping("/flights/{id}")
    public String flightDetail(@PathVariable Long id, Model model) {
        return flightService.findById(id).map(flight -> {
            model.addAttribute("flight", flight);
            model.addAttribute("log", flightService.getLog(flight));
            return "flights/detail";
        }).orElse("redirect:/flights");
    }

    /**
     * Отображает страницу «Об авторе».
     *
     * @return имя шаблона about
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
