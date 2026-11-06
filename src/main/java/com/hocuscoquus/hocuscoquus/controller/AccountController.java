package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
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
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
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

    @PostMapping("/delete")
    public String deleteAccount(
            @AuthenticationPrincipal AccountDetails details,
            @RequestParam String password,
            Authentication auth,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {
        try {
            accountService.deleteAccount(details.getAccountId(), details.getUsername(), password);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("deleteError", "Invalid password.");
            return "account-delete";
        }
        new SecurityContextLogoutHandler().logout(request, response, auth);
        return "login";
    }

    @PutMapping
    public Account updateAccount(@RequestBody Account account) {
        return accountService.updateAccount(account);
    }

}