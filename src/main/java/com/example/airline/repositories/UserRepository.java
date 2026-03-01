package com.example.airline.repositories;

import com.example.airline.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями системы.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по логину.
     *
     * @param login логин пользователя
     * @return пользователь, если найден
     */
    Optional<User> findByLogin(String login);
}
