package ru.globus.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сущность пользователя системы.
 * Содержит персональные данные и список связанных банковских счетов.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    /** Уникальный идентификатор пользователя. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** Адрес электронной почты, используется как уникальный логин. */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /** Имя пользователя. */
    @Column(name = "firstname", nullable = false)
    private String firstname;

    /** Фамилия пользователя. */
    @Column(name = "surname", nullable = false)
    private String surname;

    /** Отчество пользователя (необязательное поле). */
    @Column(name = "middlename")
    private String middlename;

    /** Дата и время регистрации пользователя в системе. */
    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    /** Список банковских счетов, принадлежащих пользователю. */
    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<BankAccount> accounts = new ArrayList<>();

    /**
     * Добавление нового счёта пользователю.
     * Метод автоматически устанавливает владельца счёта.
     *
     * @param account объект банковского счёта
     */
    public void addAccount(BankAccount account) {
        accounts.add(account);
        account.setUser(this);
    }

    /**
     * Удаление счёта у пользователя.
     * Метод обнуляет ссылку на владельца в объекте счёта.
     *
     * @param account объект банковского счёта
     */
    public void removeAccount(BankAccount account) {
        accounts.remove(account);
        account.setUser(null);
    }
}
