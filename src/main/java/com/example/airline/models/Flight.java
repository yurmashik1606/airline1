package com.example.airline.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Центральная сущность системы — авиарейс.
 * Хранит плановые и фактические данные о рейсе,
 * а также связанный журнал событий.
 * Является дочерней сущностью по отношению к {@link Aircraft} и {@link Airport},
 * и родительской — по отношению к {@link FlightLog}.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Entity
@Table(name = "flights")
public class Flight {

    /** Уникальный идентификатор рейса. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Номер рейса (например, SU-1234). */
    @Column(name = "flight_number", nullable = false, unique = true, length = 10)
    private String flightNumber;

    /** Аэропорт вылета. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;

    /** Аэропорт прилёта. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;

    /** Воздушное судно, выполняющее рейс. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    /** Плановая дата и время вылета. */
    @Column(name = "planned_departure", nullable = false)
    private LocalDateTime plannedDeparture;

    /** Плановая дата и время прилёта. */
    @Column(name = "planned_arrival", nullable = false)
    private LocalDateTime plannedArrival;

    /** Фактическое время вылета. Заполняется при переводе рейса в статус «Вылетел». */
    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;

    /** Фактическое время прилёта. Заполняется при переводе рейса в статус «Выполнен». */
    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;

    /** Количество пассажиров в эконом-классе. */
    @Column(name = "economy_passengers")
    private Integer economyPassengers = 0;

    /** Количество пассажиров в бизнес-классе. */
    @Column(name = "business_passengers")
    private Integer businessPassengers = 0;

    /**
     * Текущий статус рейса.
     * Допустимые значения: SCHEDULED, CHECK_IN, DEPARTED, ARRIVED, DELAYED, CANCELLED.
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "SCHEDULED";

    /**
     * Журнал событий рейса (дочерняя коллекция).
     * Каждое изменение статуса фиксируется в этой коллекции.
     */
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("eventTime ASC")
    private List<FlightLog> eventLog;

    public Flight() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport departureAirport) { this.departureAirport = departureAirport; }

    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport arrivalAirport) { this.arrivalAirport = arrivalAirport; }

    public Aircraft getAircraft() { return aircraft; }
    public void setAircraft(Aircraft aircraft) { this.aircraft = aircraft; }

    public LocalDateTime getPlannedDeparture() { return plannedDeparture; }
    public void setPlannedDeparture(LocalDateTime plannedDeparture) { this.plannedDeparture = plannedDeparture; }

    public LocalDateTime getPlannedArrival() { return plannedArrival; }
    public void setPlannedArrival(LocalDateTime plannedArrival) { this.plannedArrival = plannedArrival; }

    public LocalDateTime getActualDeparture() { return actualDeparture; }
    public void setActualDeparture(LocalDateTime actualDeparture) { this.actualDeparture = actualDeparture; }

    public LocalDateTime getActualArrival() { return actualArrival; }
    public void setActualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; }

    public Integer getEconomyPassengers() { return economyPassengers; }
    public void setEconomyPassengers(Integer economyPassengers) { this.economyPassengers = economyPassengers; }

    public Integer getBusinessPassengers() { return businessPassengers; }
    public void setBusinessPassengers(Integer businessPassengers) { this.businessPassengers = businessPassengers; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<FlightLog> getEventLog() { return eventLog; }
    public void setEventLog(List<FlightLog> eventLog) { this.eventLog = eventLog; }

    /**
     * Вычисляет задержку вылета в минутах.
     * Возвращает 0 если фактическое время не зафиксировано или задержки нет.
     *
     * @return задержка в минутах (0 если рейс вовремя или ещё не вылетел)
     */
    public long getDelayMinutes() {
        if (actualDeparture == null) return 0;
        long diff = java.time.Duration.between(plannedDeparture, actualDeparture).toMinutes();
        return Math.max(0, diff);
    }

    /**
     * Возвращает маршрут рейса в виде строки.
     *
     * @return строка вида "SVO → LED"
     */
    public String getRoute() {
        if (departureAirport == null || arrivalAirport == null) return "";
        return departureAirport.getIataCode() + " → " + arrivalAirport.getIataCode();
    }
}
