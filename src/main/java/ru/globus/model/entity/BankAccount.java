package ru.globus.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.globus.model.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность банковского счёта пользователя.
 * Хранит данные о владельце, валюте, номере счёта, балансе и статусе активности.
 * Также содержит базовые операции по счёту (пополнение и снятие средств).
 *
 * @author Vladlen Korablev
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "bank_accounts", uniqueConstraints = @UniqueConstraint(columnNames = {"account_number"}))
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", length = 3, nullable = false)
    private CurrencyCode currencyCode;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(name = "balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public void deposit(BigDecimal amount) {
        if (amount.signum() <= 0)
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.signum() <= 0)
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        if (balance.compareTo(amount) < 0)
            throw new IllegalStateException("Недостаточно средств на счёте");
        balance = balance.subtract(amount);
    }
}
