package com.hackathon.finservice.Util;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.Transaction;
import com.hackathon.finservice.Entities.TransactionStatus;
import com.hackathon.finservice.Entities.TransactionType;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvestmentInterestScheduler {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public InvestmentInterestScheduler(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Scheduled(fixedRate = 10000)  // cada 10 segundos
    public void applyInterestToInvestAccounts() {
        List<Account> investAccounts = accountRepository.findAll().stream()
                .filter(account -> account.getAccountType() == AccountType.INVEST)
                .toList();

        for (Account account : investAccounts) {
            double interest = account.getBalance() * 0.10;
            account.setBalance(account.getBalance() + interest);
            accountRepository.save(account);

            // registrar la transacción de interés
            Transaction interestTransaction = new Transaction();
            interestTransaction.setSourceAccount(account);
            interestTransaction.setAmount(interest);
            interestTransaction.setTransactionType(TransactionType.CASH_DEPOSIT);
            interestTransaction.setTransactionStatus(TransactionStatus.APPROVED);
            transactionRepository.save(interestTransaction);
        }
    }
}
