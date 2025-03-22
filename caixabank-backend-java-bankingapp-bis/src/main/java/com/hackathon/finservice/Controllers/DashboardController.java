package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Service.AccountService;
import com.hackathon.finservice.Service.UserService;
import com.hackathon.finservice.Util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserService userService;
    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    public DashboardController(UserService userService, AccountService accountService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        return userService.findByEmail(email)
                .map(user -> {
                    Account mainAccount = user.getAccounts().stream()
                            .filter(acc -> acc.getAccountType() == AccountType.MAIN)
                            .findFirst()
                            .orElse(null);

                    if (mainAccount == null) {
                        return ResponseEntity.status(404).body(Map.of("error", "Main account not found"));
                    }

                    return ResponseEntity.ok(Map.of(
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "accountNumber", mainAccount.getAccountNumber(),
                            "accountType", "Main",
                            "hashedPassword", user.getPassword()
                    ));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "User not found")));
    }

    @GetMapping("/account")
    public ResponseEntity<?> getMainAccount(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        return accountService.getMainAccountByEmail(email)
                .map(account -> ResponseEntity.ok(Map.of(
                        "accountNumber", account.getAccountNumber(),
                        "balance", account.getBalance(),
                        "accountType", "Main"
                )))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Main account not found")));
    }

    @GetMapping("/account/{index}")
    public ResponseEntity<?> getAccountByIndex(@RequestHeader("Authorization") String token, @PathVariable int index) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        var accounts = accountService.getAllAccountsByEmail(email);

        if (index < 0 || index >= accounts.size()) {
            return ResponseEntity.status(404).body(Map.of("error", "Account not found at index " + index));
        }

        Account account = accounts.get(index);
        return ResponseEntity.ok(Map.of(
                "accountNumber", account.getAccountNumber(),
                "balance", account.getBalance(),
                "accountType", account.getAccountType().toString()
        ));
    }
}
