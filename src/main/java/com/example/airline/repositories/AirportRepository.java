package com.example.airline.repositories;

import com.example.airline.models.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Репозиторий для работы с аэропортами.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    /**
     * Находит аэропорт по коду ИАТА.
     *
     * @param iataCode трёхбуквенный код ИАТА
     * @return аэропорт, если найден
     */
    Optional<Airport> findByIataCode(String iataCode);
}
