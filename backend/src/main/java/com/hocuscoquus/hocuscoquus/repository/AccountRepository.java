package com.hocuscoquus.hocuscoquus.repository;

import com.hocuscoquus.hocuscoquus.model.Account;

public interface AccountRepository {
    Account findById(long accountId);
    Account findByEmail(String email);
}
