package com.example.airline.models;

import jakarta.persistence.*;
import java.util.List;

/**
 * Сущность воздушного судна.
 * Представляет единицу парка воздушных судов авиакомпании.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Entity
@Table(name = "aircraft")
public class Aircraft {

    /** Уникальный идентификатор воздушного судна. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Модель воздушного судна (например, Boeing 737-800). */
    @Column(name = "model", nullable = false)
    private String model;

    /** Регистрационный номер воздушного судна (например, VP-BXA). */
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    /** Количество мест в эконом-классе. */
    @Column(name = "economy_seats", nullable = false)
    private Integer economySeats;

    /** Количество мест в бизнес-классе. */
    @Column(name = "business_seats", nullable = false)
    private Integer businessSeats;

    /** Статус воздушного судна: В эксплуатации / На техобслуживании. */
    @Column(name = "status", nullable = false)
    private String status;

    /** Рейсы, назначенные на данное воздушное судно. */
    @OneToMany(mappedBy = "aircraft")
    private List<Flight> flights;

    public Aircraft() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public Integer getEconomySeats() { return economySeats; }
    public void setEconomySeats(Integer economySeats) { this.economySeats = economySeats; }

    public Integer getBusinessSeats() { return businessSeats; }
    public void setBusinessSeats(Integer businessSeats) { this.businessSeats = businessSeats; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }

    /**
     * Возвращает общую вместимость воздушного судна.
     *
     * @return сумма мест в эконом- и бизнес-классах
     */
    public int getTotalSeats() {
        return economySeats + businessSeats;
    }
}
