package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.service.AccountService;

@Controller
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/signup")
    public String createAccount(
            @RequestParam String firstName,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {
        try {
            accountService.createAccount(firstName, email, password);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("signupError", ex.getMessage());
            model.addAttribute("firstName", firstName);
            model.addAttribute("email", email);
            return "signup";
        }
        return "login";
    }

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@PathVariable long accountId, @RequestBody Account account) {
        accountService.deleteAccount(accountId, account.getEmail(), account.getPassword());
    }

    @PutMapping
    public Account updateAccount(@RequestBody Account account) {
        return accountService.updateAccount(account);
    }

}
