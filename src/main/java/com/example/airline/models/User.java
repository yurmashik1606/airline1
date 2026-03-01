package com.example.airline.models;

import jakarta.persistence.*;

/**
 * Сущность пользователя системы.
 * Может быть диспетчером или администратором авиакомпании.
 *
 * @author Студент группы _______
 * @version 1.0
 */
@Entity
@Table(name = "users")
public class User {

    /** Уникальный идентификатор пользователя. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Логин пользователя (используется для входа в систему). */
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    /** Хеш пароля пользователя (BCrypt). */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** Имя пользователя. */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /** Фамилия пользователя. */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Роль пользователя в системе.
     * Допустимые значения: ROLE_DISPATCHER, ROLE_ADMIN.
     */
    @Column(name = "role", nullable = false)
    private String role;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    /**
     * Возвращает полное имя пользователя.
     *
     * @return строка вида "Иванов Иван"
     */
    public String getFullName() {
        return lastName + " " + firstName;
    }
}
