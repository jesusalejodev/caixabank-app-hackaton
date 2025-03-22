package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.DTO.TransactionDTO;
import com.hackathon.finservice.Service.AccountService;
import com.hackathon.finservice.Service.TransactionService;
import com.hackathon.finservice.Util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    public AccountController(TransactionService transactionService, AccountService accountService, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody Map<String, String> request) {
        String mainAccountNumber = request.get("accountNumber");
        String accountType = request.get("accountType");

        if (mainAccountNumber == null || accountType == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Account number and account type are required"));
        }

        return accountService.createAccount(mainAccountNumber, accountType)
                .map(account -> ResponseEntity.ok(Map.of("msg", "New account added successfully for user")))
                .orElse(ResponseEntity.status(400).body(Map.of("error", "Invalid account type or main account not found")));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestHeader("Authorization") String token, @RequestBody Map<String, Double> request) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Double amount = request.get("amount");

        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Amount must be greater than zero"));
        }

        return accountService.getMainAccountByEmail(email)
                .map(account -> ResponseEntity.ok(Map.of("msg", transactionService.deposit(account.getAccountNumber(), amount))))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Main account not found")));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestHeader("Authorization") String token, @RequestBody Map<String, Double> request) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Double amount = request.get("amount");

        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Amount must be greater than zero"));
        }

        return accountService.getMainAccountByEmail(email)
                .map(account -> ResponseEntity.ok(Map.of("msg", transactionService.withdraw(account.getAccountNumber(), amount))))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Main account not found")));
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<?> transfer(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> request) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        String targetAccountNumber = (String) request.get("targetAccountNumber");
        Double amount = (Double) request.get("amount");

        if (targetAccountNumber == null || amount == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Target account number and amount are required"));
        }

        return accountService.getMainAccountByEmail(email)
                .map(sourceAccount -> {
                    String message = transactionService.transfer(sourceAccount.getAccountNumber(), targetAccountNumber, amount);
                    return ResponseEntity.ok(Map.of("msg", message));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Main account not found")));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        List<TransactionDTO> transactions = transactionService.getAllTransactionsForUser(email);
        return ResponseEntity.ok(transactions);
    }
}
