package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.handler.AccountHandler;
import com.hocuscoquus.hocuscoquus.model.AccountUpdateRequest;
import com.hocuscoquus.hocuscoquus.service.AccountService;

@Controller
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;
    private final AccountHandler accountHandler;

    public AccountController(AccountService accountService, AccountHandler accountHandler) {
        this.accountService = accountService;
        this.accountHandler = accountHandler;
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
        return "redirect:/";
    }

    @PatchMapping("/api/update")
    @ResponseBody
    public ResponseEntity<Void> updateAccount(
            @AuthenticationPrincipal AccountDetails details,
            @Valid @RequestBody AccountUpdateRequest request) {

        if (details == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return accountHandler.processAccountUpdateRequest(
                details.getAccountId(), request);
    }
}