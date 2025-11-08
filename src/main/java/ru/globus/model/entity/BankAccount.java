package ru.globus.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import ru.globus.model.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность банковского счёта пользователя.
 * Хранит данные о владельце, валюте, номере счёта, балансе и статусе активности.
 * Также содержит базовые операции по счёту (пополнение и снятие средств).
 */
@Getter
@Setter
@Entity
@Table(
    name = "bank_accounts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_number"})
)
@NoArgsConstructor
public class BankAccount {

    /** Уникальный идентификатор счёта. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** Владелец счёта (пользователь системы). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Валюта счёта (например, RUB, USD, EUR). */
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", length = 3, nullable = false)
    private CurrencyCode currencyCode;

    /** Уникальный номер банковского счёта. */
    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    /** Баланс счёта с точностью до двух знаков после запятой. */
    @Column(name = "balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    /** Флаг активности счёта. Если false — операции запрещены. */
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    /** Дата и время создания счёта. */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Конструктор для создания нового банковского счёта.
     * @param user владелец счёта
     * @param currencyCode код валюты счёта
     * @param accountNumber уникальный номер счёта
     */
    public BankAccount(User user, CurrencyCode currencyCode, String accountNumber) {
        this.user = user;
        this.currencyCode = currencyCode;
        this.accountNumber = accountNumber;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    /**
     * Пополнение счёта на указанную сумму.
     * @param amount сумма пополнения (должна быть положительной)
     * @throws IllegalArgumentException если сумма меньше или равна нулю
     */
    public void deposit(BigDecimal amount) {
        if (amount.signum() <= 0)
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        balance = balance.add(amount);
    }

    /**
     * Снятие средств со счёта.
     * @param amount сумма снятия (должна быть положительной)
     * @throws IllegalArgumentException если сумма меньше или равна нулю
     * @throws IllegalStateException если на счёте недостаточно средств
     */
    public void withdraw(BigDecimal amount) {
        if (amount.signum() <= 0)
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        if (balance.compareTo(amount) < 0)
            throw new IllegalStateException("Недостаточно средств на счёте");
        balance = balance.subtract(amount);
    }

    /** Переопределение equals() с учётом Hibernate-прокси. */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ?
            proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ?
            proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BankAccount that = (BankAccount) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    /** Переопределение hashCode() с учётом Hibernate-прокси. */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy
            ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
            : getClass().hashCode();
    }
}
