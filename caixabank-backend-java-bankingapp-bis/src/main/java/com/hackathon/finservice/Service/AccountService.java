package com.hackathon.finservice.Service;

import com.hackathon.finservice.Entities.*;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<Account> getAllAccountsByEmail(String email) {
        return accountRepository.findByUser_Email(email);
    }

    @Transactional
    public Optional<Account> getMainAccountByEmail(String email) {
        List<Account> accounts = accountRepository.findByUser_Email(email);
        return accounts.stream().filter(acc -> acc.getAccountType() == AccountType.MAIN).findFirst();
    }

    @Transactional
    public Optional<Account> createAccount(String mainAccountNumber, String accountTypeStr) {
        Optional<Account> mainAccountOptional = accountRepository.findByAccountNumber(mainAccountNumber);

        if (mainAccountOptional.isEmpty()) {
            return Optional.empty();  // no existe la cuenta principal
        }

        Account mainAccount = mainAccountOptional.get();
        User user = mainAccount.getUser();

        AccountType accountType;
        try {
            accountType = AccountType.valueOf(accountTypeStr.toUpperCase());  // "Invest" -> INVEST
        } catch (IllegalArgumentException e) {
            return Optional.empty();  // accountType inválido
        }

        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setAccountType(accountType);
        accountRepository.save(newAccount);

        return Optional.of(newAccount);
    }
}
