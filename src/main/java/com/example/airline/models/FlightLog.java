package com.example.airline.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Запись журнала событий рейса.
 * Является дочерней сущностью по отношению к {@link Flight}.
 * Фиксирует каждое изменение статуса рейса.
 * Записи журнала не могут быть удалены — обеспечивают аудиторский след.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Entity
@Table(name = "flight_log")
public class FlightLog {

    /** Уникальный идентификатор записи журнала. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Рейс, к которому относится запись (родительская сущность). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    /** Статус рейса до изменения. */
    @Column(name = "previous_status", nullable = false, length = 20)
    private String previousStatus;

    /** Новый статус рейса после изменения. */
    @Column(name = "new_status", nullable = false, length = 20)
    private String newStatus;

    /** Дата и время фиксации события. */
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    /**
     * Причина изменения статуса.
     * Обязательна для статусов DELAYED и CANCELLED.
     */
    @Column(name = "reason", length = 500)
    private String reason;

    /** Пользователь (диспетчер или администратор), выполнивший изменение. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private User operator;

    public FlightLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public User getOperator() { return operator; }
    public void setOperator(User operator) { this.operator = operator; }
}
