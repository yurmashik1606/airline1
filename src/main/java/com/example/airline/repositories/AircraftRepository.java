package com.example.airline.repositories;

import com.example.airline.models.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с воздушными судами.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    /**
     * Находит воздушное судно по регистрационному номеру.
     *
     * @param registrationNumber регистрационный номер (например, VP-BXA)
     * @return воздушное судно, если найдено
     */
    Optional<Aircraft> findByRegistrationNumber(String registrationNumber);

    /**
     * Возвращает список воздушных судов по статусу.
     *
     * @param status статус (например, "В эксплуатации")
     * @return список воздушных судов
     */
    List<Aircraft> findByStatus(String status);
}
