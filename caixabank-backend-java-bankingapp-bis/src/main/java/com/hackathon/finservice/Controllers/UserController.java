package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Service.UserService;
import com.hackathon.finservice.Util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        if (!request.containsKey("name") || !request.containsKey("email") || !request.containsKey("password")) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }

        try {
            User user = userService.registerUser(request.get("name"), request.get("email"), request.get("password"));
            Account mainAccount = user.getAccounts().stream()
                    .filter(acc -> acc.getAccountType() == AccountType.MAIN)
                    .findFirst()
                    .orElseThrow();

            return ResponseEntity.ok(Map.of(
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "accountNumber", mainAccount.getAccountNumber(),
                    "accountType", "Main",
                    "hashedPassword", user.getPassword()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        return userService.findByEmail(request.get("identifier"))
                .filter(user -> userService.matchPassword(request.get("password"), user.getPassword()))
                .map(user -> ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(user.getEmail()))))
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Bad credentials")));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("msg", "User logged out successfully"));
    }
}
