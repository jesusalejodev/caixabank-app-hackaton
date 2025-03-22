package com.hackathon.finservice.DTO;

import com.hackathon.finservice.Entities.Transaction;
import com.hackathon.finservice.Entities.TransactionStatus;
import com.hackathon.finservice.Entities.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TransactionDTO {

    private Long id;
    private Double amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private Date transactionDate;
    private String sourceAccountNumber;
    private String targetAccountNumber;

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.transactionType = transaction.getTransactionType();
        this.transactionStatus = transaction.getTransactionStatus();
        this.transactionDate = transaction.getTransactionDate();
        this.sourceAccountNumber = transaction.getSourceAccount().getAccountNumber();
        this.targetAccountNumber = (transaction.getTargetAccount() != null)
                ? transaction.getTargetAccount().getAccountNumber()
                : "N/A";
    }

}
