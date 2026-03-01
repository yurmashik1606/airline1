package com.example.airline.repositories;

import com.example.airline.models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с рейсами.
 * Содержит запросы для фильтрации и поиска рейсов.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Находит рейс по его номеру.
     *
     * @param flightNumber номер рейса (например, SU-1234)
     * @return рейс, если найден
     */
    Optional<Flight> findByFlightNumber(String flightNumber);

    /**
     * Возвращает рейсы по статусу, отсортированные по времени вылета.
     *
     * @param status статус рейса
     * @return список рейсов
     */
    List<Flight> findByStatusOrderByPlannedDepartureAsc(String status);

    /**
     * Ищет рейсы с пересечением временного интервала для воздушного судна.
     * Используется для проверки конфликта назначения ВС.
     *
     * @param aircraftId идентификатор воздушного судна
     * @param departure  время вылета нового рейса
     * @param arrival    время прилёта нового рейса
     * @return список конфликтующих рейсов
     */
    @Query("SELECT f FROM Flight f WHERE f.aircraft.id = :aircraftId " +
           "AND f.status != 'CANCELLED' " +
           "AND f.plannedDeparture < :arrival AND f.plannedArrival > :departure")
    List<Flight> findConflictingFlights(@Param("aircraftId") Long aircraftId,
                                        @Param("departure") LocalDateTime departure,
                                        @Param("arrival") LocalDateTime arrival);

    /**
     * Выполняет поиск рейсов по номеру рейса или коду аэропорта вылета/прилёта.
     *
     * @param query строка поиска
     * @return список найденных рейсов
     */
    @Query("SELECT f FROM Flight f WHERE " +
           "LOWER(f.flightNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.departureAirport.iataCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.arrivalAirport.iataCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Flight> searchByQuery(@Param("query") String query);

    /**
     * Возвращает рейсы за указанный период для расчёта статистики пунктуальности.
     *
     * @param from начало периода
     * @param to   конец периода
     * @return список выполненных рейсов за период
     */
    @Query("SELECT f FROM Flight f WHERE f.status = 'ARRIVED' " +
           "AND f.plannedDeparture BETWEEN :from AND :to")
    List<Flight> findCompletedBetween(@Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to);
}
