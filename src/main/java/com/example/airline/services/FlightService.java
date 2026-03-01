package com.example.airline.services;

import com.example.airline.exceptions.AircraftConflictException;
import com.example.airline.exceptions.InvalidStatusTransitionException;
import com.example.airline.models.*;
import com.example.airline.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сервис управления рейсами авиакомпании.
 * Содержит основную бизнес-логику системы учёта:
 * изменение статуса рейсов с журналированием,
 * проверку конфликтов назначения воздушных судов,
 * поиск и фильтрацию рейсов.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Service
public class FlightService {

    /**
     * Допустимые переходы между статусами рейса.
     * Ключ — текущий статус, значение — множество допустимых следующих статусов.
     */
    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            "SCHEDULED", Set.of("CHECK_IN", "DELAYED", "CANCELLED"),
            "CHECK_IN",  Set.of("DEPARTED", "DELAYED", "CANCELLED"),
            "DEPARTED",  Set.of("ARRIVED"),
            "DELAYED",   Set.of("CHECK_IN", "CANCELLED"),
            "ARRIVED",   Set.of(),
            "CANCELLED", Set.of()
    );

    private final FlightRepository flightRepository;
    private final FlightLogRepository flightLogRepository;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param flightRepository    репозиторий рейсов
     * @param flightLogRepository репозиторий журнала событий
     */
    public FlightService(FlightRepository flightRepository,
                         FlightLogRepository flightLogRepository) {
        this.flightRepository = flightRepository;
        this.flightLogRepository = flightLogRepository;
    }

    /**
     * Возвращает все рейсы, отсортированные по плановому времени вылета.
     *
     * @return список всех рейсов
     */
    public List<Flight> findAll() {
        return flightRepository.findAll(
                org.springframework.data.domain.Sort.by("plannedDeparture"));
    }

    /**
     * Находит рейс по идентификатору.
     *
     * @param id идентификатор рейса
     * @return рейс или пустой Optional
     */
    public Optional<Flight> findById(Long id) {
        return flightRepository.findById(id);
    }

    /**
     * Выполняет поиск рейсов по строке запроса (номер рейса или код аэропорта).
     *
     * @param query строка поиска
     * @return список найденных рейсов
     */
    public List<Flight> search(String query) {
        if (query == null || query.isBlank()) return findAll();
        return flightRepository.searchByQuery(query);
    }

    /**
     * Сохраняет новый рейс с проверкой конфликта назначения воздушного судна.
     *
     * @param flight данные нового рейса
     * @return сохранённый рейс
     * @throws AircraftConflictException если воздушное судно занято в указанный период
     */
    @Transactional
    public Flight save(Flight flight) {
        checkAircraftConflict(
                flight.getAircraft().getId(),
                flight.getPlannedDeparture(),
                flight.getPlannedArrival(),
                flight.getId()
        );
        if (flight.getStatus() == null) flight.setStatus("SCHEDULED");
        return flightRepository.save(flight);
    }

    /**
     * Удаляет рейс по идентификатору.
     *
     * @param id идентификатор рейса
     */
    public void deleteById(Long id) {
        flightRepository.deleteById(id);
    }

    /**
     * Изменяет статус рейса с обязательным созданием записи в журнале событий.
     * Весь метод выполняется атомарно в рамках одной транзакции.
     *
     * @param flightId    идентификатор рейса
     * @param newStatus   новый статус рейса
     * @param reason      причина (обязательна для DELAYED и CANCELLED)
     * @param actualTime  фактическое время события (для DEPARTED и ARRIVED)
     * @param operator    пользователь, выполняющий операцию
     * @return обновлённый рейс
     * @throws InvalidStatusTransitionException если переход недопустим
     * @throws IllegalArgumentException         если рейс не найден
     */
    @Transactional
    public Flight changeStatus(Long flightId, String newStatus, String reason,
                                LocalDateTime actualTime, User operator) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Рейс с ID " + flightId + " не найден."));

        String currentStatus = flight.getStatus();

        if (!ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of()).contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Переход из статуса «" + currentStatus + "» в «" + newStatus + "» недопустим.");
        }

        if (actualTime != null) {
            if ("DEPARTED".equals(newStatus)) flight.setActualDeparture(actualTime);
            if ("ARRIVED".equals(newStatus))  flight.setActualArrival(actualTime);
        }

        flight.setStatus(newStatus);
        flightRepository.save(flight);

        FlightLog log = new FlightLog();
        log.setFlight(flight);
        log.setPreviousStatus(currentStatus);
        log.setNewStatus(newStatus);
        log.setEventTime(LocalDateTime.now());
        log.setReason(reason);
        log.setOperator(operator);
        flightLogRepository.save(log);

        return flight;
    }

    /**
     * Проверяет наличие временного конфликта для воздушного судна.
     *
     * @param aircraftId      идентификатор воздушного судна
     * @param departure       время вылета
     * @param arrival         время прилёта
     * @param excludeFlightId рейс, исключаемый из проверки (при редактировании)
     * @throws AircraftConflictException если конфликт обнаружен
     */
    public void checkAircraftConflict(Long aircraftId, LocalDateTime departure,
                                       LocalDateTime arrival, Long excludeFlightId) {
        flightRepository.findConflictingFlights(aircraftId, departure, arrival)
                .stream()
                .filter(f -> !f.getId().equals(excludeFlightId))
                .findFirst()
                .ifPresent(f -> {
                    throw new AircraftConflictException(
                            "Воздушное судно уже назначено на рейс " +
                            f.getFlightNumber() + " (" +
                            f.getPlannedDeparture() + " — " + f.getPlannedArrival() + ").");
                });
    }

    /**
     * Возвращает журнал событий для указанного рейса.
     *
     * @param flight рейс
     * @return список записей журнала в хронологическом порядке
     */
    public List<FlightLog> getLog(Flight flight) {
        return flightLogRepository.findByFlightOrderByEventTimeAsc(flight);
    }

    /**
     * Рассчитывает показатель пунктуальности (OTP) за указанный период.
     * Рейс считается пунктуальным, если задержка не превышает 15 минут.
     *
     * @param from начало периода
     * @param to   конец периода
     * @return процент пунктуальных рейсов (0–100)
     */
    public double calculateOtp(LocalDateTime from, LocalDateTime to) {
        List<Flight> completed = flightRepository.findCompletedBetween(from, to);
        if (completed.isEmpty()) return 0.0;
        long onTime = completed.stream()
                .filter(f -> f.getDelayMinutes() <= 15)
                .count();
        return (double) onTime / completed.size() * 100.0;
    }
}
