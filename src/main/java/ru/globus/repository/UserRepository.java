package ru.globus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.globus.model.entity.User;

import java.util.UUID;

/**
 * Репозиторий для работы с сущностью User.
 * Содержит базовые CRUD-операции и дополнительные методы поиска.
 *
 * @author Vladlen Korablev
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Проверяет, существует ли пользователь с указанным email.
     *
     * @param email адрес электронной почты
     * @return true, если пользователь существует
     */
    boolean existsByEmail(String email);
}
