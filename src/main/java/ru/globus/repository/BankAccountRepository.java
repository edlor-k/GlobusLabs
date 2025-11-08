package ru.globus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.globus.model.entity.BankAccount;

import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    boolean existsByAccountNumber(String number);
}
