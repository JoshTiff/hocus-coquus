package com.hocuscoquus.hocuscoquus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findById(long accountId);
    Account findByEmail(String email);
}
