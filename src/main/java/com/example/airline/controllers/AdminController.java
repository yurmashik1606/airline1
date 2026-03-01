package com.example.airline.controllers;

import com.example.airline.exceptions.AircraftConflictException;
import com.example.airline.exceptions.InvalidStatusTransitionException;
import com.example.airline.models.*;
import com.example.airline.repositories.*;
import com.example.airline.services.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

/**
 * Контроллер административной панели.
 * Доступен только пользователям с ролью ROLE_ADMIN.
 * Обрабатывает операции управления рейсами, воздушными судами и аэропортами.
 *
 * @author Студент группы ДЦПУП23-1
 * @version 1.0
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FlightService flightService;
    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final AirportRepository airportRepository;
    private final UserService userService;

    public AdminController(FlightService flightService,
                           FlightRepository flightRepository,
                           AircraftRepository aircraftRepository,
                           AirportRepository airportRepository,
                           UserService userService) {
        this.flightService = flightService;
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
        this.airportRepository = airportRepository;
        this.userService = userService;
    }

    /**
     * Отображает главную страницу административной панели.
     *
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона admin/dashboard
     */
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("flights", flightService.findAll());
        model.addAttribute("aircraft", aircraftRepository.findAll());
        model.addAttribute("airports", airportRepository.findAll());
        long scheduled = flightService.findAll().stream().filter(f -> "SCHEDULED".equals(f.getStatus())).count();
        long departed  = flightService.findAll().stream().filter(f -> "DEPARTED".equals(f.getStatus())).count();
        long delayed   = flightService.findAll().stream().filter(f -> "DELAYED".equals(f.getStatus())).count();
        model.addAttribute("scheduledCount", scheduled);
        model.addAttribute("departedCount", departed);
        model.addAttribute("delayedCount", delayed);
        double otp = flightService.calculateOtp(LocalDateTime.now().minusDays(30), LocalDateTime.now());
        model.addAttribute("otp", String.format("%.1f", otp));
        return "admin/dashboard";
    }

    /**
     * Отображает форму добавления нового рейса.
     *
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона admin/flight-form
     */
    @GetMapping("/flights/new")
    public String newFlightForm(Model model) {
        model.addAttribute("flight", new Flight());
        model.addAttribute("aircraft", aircraftRepository.findAll());
        model.addAttribute("airports", airportRepository.findAll());
        return "admin/flight-form";
    }

    /**
     * Сохраняет новый или обновлённый рейс.
     *
     * @param flightNumber      номер рейса
     * @param departureAirportId идентификатор аэропорта вылета
     * @param arrivalAirportId   идентификатор аэропорта прилёта
     * @param aircraftId         идентификатор воздушного судна
     * @param plannedDeparture   плановое время вылета
     * @param plannedArrival     плановое время прилёта
     * @param redirectAttributes атрибуты для передачи сообщений при редиректе
     * @return редирект на панель администратора
     */
    @PostMapping("/flights/save")
    public String saveFlight(@RequestParam String flightNumber,
                              @RequestParam Long departureAirportId,
                              @RequestParam Long arrivalAirportId,
                              @RequestParam Long aircraftId,
                              @RequestParam String plannedDeparture,
                              @RequestParam String plannedArrival,
                              RedirectAttributes redirectAttributes) {
        try {
            Flight flight = new Flight();
            flight.setFlightNumber(flightNumber);
            flight.setDepartureAirport(airportRepository.findById(departureAirportId).orElseThrow());
            flight.setArrivalAirport(airportRepository.findById(arrivalAirportId).orElseThrow());
            flight.setAircraft(aircraftRepository.findById(aircraftId).orElseThrow());
            flight.setPlannedDeparture(LocalDateTime.parse(plannedDeparture));
            flight.setPlannedArrival(LocalDateTime.parse(plannedArrival));
            flightService.save(flight);
            redirectAttributes.addFlashAttribute("success", "Рейс " + flightNumber + " успешно добавлен.");
        } catch (AircraftConflictException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при сохранении рейса: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    /**
     * Удаляет рейс по идентификатору.
     *
     * @param id                 идентификатор рейса
     * @param redirectAttributes атрибуты для передачи сообщений при редиректе
     * @return редирект на панель администратора
     */
    @PostMapping("/flights/delete/{id}")
    public String deleteFlight(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            flightService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Рейс успешно удалён.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    /**
     * Обновляет статус рейса.
     *
     * @param flightId   идентификатор рейса
     * @param newStatus  новый статус
     * @param reason     причина изменения (для задержки/отмены)
     * @param actualTime фактическое время (для вылета/прилёта)
     * @param userDetails данные текущего пользователя
     * @param redirectAttributes атрибуты для передачи сообщений
     * @return редирект на страницу рейса
     */
    @PostMapping("/flights/status")
    public String changeStatus(@RequestParam Long flightId,
                                @RequestParam String newStatus,
                                @RequestParam(required = false) String reason,
                                @RequestParam(required = false) String actualTime,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            User operator = userService.findByLogin(userDetails.getUsername());
            LocalDateTime actualDateTime = (actualTime != null && !actualTime.isBlank())
                    ? LocalDateTime.parse(actualTime) : null;
            flightService.changeStatus(flightId, newStatus, reason, actualDateTime, operator);
            redirectAttributes.addFlashAttribute("success", "Статус рейса успешно обновлён.");
        } catch (InvalidStatusTransitionException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении статуса: " + e.getMessage());
        }
        return "redirect:/flights/" + flightId;
    }
}
