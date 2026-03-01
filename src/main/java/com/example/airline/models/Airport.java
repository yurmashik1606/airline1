package com.example.airline.models;

import jakarta.persistence.*;
import java.util.List;

/**
 * Сущность аэропорта.
 * Справочник аэропортов, используемых в маршрутах авиакомпании.
 *
 * @author Студент группы ДЦПУП23-1
 * @version 1.0
 */
@Entity
@Table(name = "airports")
public class Airport {

    /** Уникальный идентификатор аэропорта. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Код ИАТА аэропорта (например, SVO, LED, AER). */
    @Column(name = "iata_code", nullable = false, unique = true, length = 3)
    private String iataCode;

    /** Полное название аэропорта. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Город расположения аэропорта. */
    @Column(name = "city", nullable = false)
    private String city;

    /** Страна расположения аэропорта. */
    @Column(name = "country", nullable = false)
    private String country;

    /** Рейсы, вылетающие из данного аэропорта. */
    @OneToMany(mappedBy = "departureAirport")
    private List<Flight> departureFlights;

    /** Рейсы, прилетающие в данный аэропорт. */
    @OneToMany(mappedBy = "arrivalAirport")
    private List<Flight> arrivalFlights;

    public Airport() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIataCode() { return iataCode; }
    public void setIataCode(String iataCode) { this.iataCode = iataCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    /**
     * Возвращает строковое представление аэропорта для отображения в интерфейсе.
     *
     * @return строка вида "SVO — Шереметьево (Москва)"
     */
    @Override
    public String toString() {
        return iataCode + " — " + name + " (" + city + ")";
    }
}
