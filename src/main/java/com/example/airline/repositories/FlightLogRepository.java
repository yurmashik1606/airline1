package com.example.airline.repositories;

import com.example.airline.models.Flight;
import com.example.airline.models.FlightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторий для работы с журналом событий рейсов.
 *
 * @author Студент группы ДЦПУП23-1
 * @version 1.0
 */
@Repository
public interface FlightLogRepository extends JpaRepository<FlightLog, Long> {

    /**
     * Возвращает хронологический журнал событий для указанного рейса.
     *
     * @param flight рейс
     * @return список записей журнала, отсортированных по времени
     */
    List<FlightLog> findByFlightOrderByEventTimeAsc(Flight flight);
}
