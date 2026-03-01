package com.example.airline.services;

import com.example.airline.models.User;
import com.example.airline.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Сервис управления пользователями системы.
 * Реализует интерфейс {@link UserDetailsService} для интеграции со Spring Security.
 *
 * @author Студент группы ДЦПУП23-1
 * @version 1.0
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userRepository  репозиторий пользователей
     * @param passwordEncoder кодировщик паролей (BCrypt)
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Загружает данные пользователя по логину для Spring Security.
     *
     * @param login логин пользователя
     * @return объект UserDetails для Spring Security
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с логином «" + login + "» не найден."));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    /**
     * Сохраняет нового пользователя с зашифрованным паролем.
     *
     * @param user     объект пользователя с незашифрованным паролем в поле passwordHash
     * @return сохранённый пользователь
     */
    public User saveUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    /**
     * Находит пользователя по логину.
     *
     * @param login логин пользователя
     * @return пользователь или null, если не найден
     */
    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }
}
