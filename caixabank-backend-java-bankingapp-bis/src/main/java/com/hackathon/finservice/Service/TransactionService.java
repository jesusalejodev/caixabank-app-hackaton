package com.hackathon.finservice.Service;

import com.hackathon.finservice.DTO.TransactionDTO;
import com.hackathon.finservice.Entities.*;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<TransactionDTO> getAllTransactionsForUser(String email) {
        List<Account> accounts = accountRepository.findByUser_Email(email);

        return accounts.stream()
                .flatMap(account -> transactionRepository.findBySourceAccount_AccountNumber(account.getAccountNumber()).stream())
                .map(TransactionDTO::new)
                .toList();
    }


    @Transactional
    public String deposit(String accountNumber, Double amount) {
        if (amount <= 0) {
            return "Amount must be greater than zero";
        }

        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if (optionalAccount.isEmpty()) {
            return "Account not found";
        }

        Account account = optionalAccount.get();

        //comisión del 2% si el depósito es mayor a 50,000
        double finalAmount = amount > 50000 ? amount * 0.98 : amount;
        account.setBalance(account.getBalance() + finalAmount);
        accountRepository.save(account);

        //registrar transacción
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        return "Cash deposited successfully";
    }

    @Transactional
    public String withdraw(String accountNumber, Double amount) {
        if (amount <= 0) {
            return "Amount must be greater than zero";
        }

        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if (optionalAccount.isEmpty()) {
            return "Account not found";
        }

        Account account = optionalAccount.get();

        //comisión del 1% si el retiro es mayor a 10,000
        double totalAmount = amount > 10000 ? amount * 1.01 : amount;

        if (account.getBalance() < totalAmount) {
            return "Insufficient balance";
        }

        account.setBalance(account.getBalance() - totalAmount);
        accountRepository.save(account);

        //registrar transacción
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        return "Cash withdrawn successfully";
    }

    @Transactional
    public String transfer(String sourceAccountNumber, String targetAccountNumber, Double amount) {
        if (amount <= 0) {
            return "Amount must be greater than zero";
        }

        Optional<Account> sourceOptional = accountRepository.findByAccountNumber(sourceAccountNumber);
        Optional<Account> targetOptional = accountRepository.findByAccountNumber(targetAccountNumber);

        if (sourceOptional.isEmpty() || targetOptional.isEmpty()) {
            return "Source or target account not found";
        }

        Account source = sourceOptional.get();
        Account target = targetOptional.get();

        // solo MAIN puede transferir dinero a INVEST
        if (target.getAccountType() == AccountType.INVEST && source.getAccountType() != AccountType.MAIN) {
            return "Only Main account can transfer money to an Invest account";
        }

        if (source.getBalance() < amount) {
            return "Insufficient balance";
        }

        // actualizar balances
        source.setBalance(source.getBalance() - amount);
        target.setBalance(target.getBalance() + amount);
        accountRepository.save(source);
        accountRepository.save(target);

        Transaction transaction = new Transaction();
        transaction.setSourceAccount(source);
        transaction.setTargetAccount(target);  //garantizamos que siempre haya targetAccount
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);

        // detectar fraude según el monto
        if (amount > 80000) {
            transaction.setTransactionStatus(TransactionStatus.FRAUD);
            transactionRepository.save(transaction);
            return "Transaction flagged as fraud";
        }

        // detección de fraude por transferencias rápidas
        List<Transaction> recentTransfers = transactionRepository.findBySourceAccount_AccountNumber(sourceAccountNumber);
        long now = System.currentTimeMillis();
        long count = recentTransfers.stream()
                .filter(t -> t.getTargetAccount() != null && t.getTargetAccount().getAccountNumber().equals(targetAccountNumber))
                .filter(t -> (now - t.getTransactionDate().getTime()) < 5000)
                .count();

        if (count >= 4) {
            transaction.setTransactionStatus(TransactionStatus.FRAUD);
            transactionRepository.save(transaction);
            return "Transaction flagged as fraud";
        }

        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        return "Fund transferred successfully";
    }

}
