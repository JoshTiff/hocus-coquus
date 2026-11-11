package com.hocuscoquus.hocuscoquus.entity;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AccountDetails implements UserDetails {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Account account;

    public AccountDetails(Account account) {
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    public long getAccountId() {
        return account.getAccountId();
    }

    public String getFirstName() {
        return account.getFirstName();
    }
    
    public Account getAccount() {
    	return account;
    }
}