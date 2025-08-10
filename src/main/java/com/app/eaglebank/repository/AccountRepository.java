package com.app.eaglebank.repository;

import com.app.eaglebank.model.Account;
import com.app.eaglebank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
    boolean existsByUser(User user);
    Optional<Account> findByAccountNumber(String accountNumber);

}


