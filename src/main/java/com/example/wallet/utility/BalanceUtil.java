package com.example.wallet.utility;

import com.example.wallet.entity.UserEntity;
import com.example.wallet.exception.InsufficientBalanceException;
import com.example.wallet.model.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceUtil {

    public BigDecimal calculateBalance(BigDecimal amount, BigDecimal balance, TransactionType transactionType, UserEntity account) {

        if (transactionType.equals(TransactionType.CREDIT)) {
            balance = balance.add(amount);
            account.setBalance(balance);
        }

        if (transactionType.equals(TransactionType.DEBIT)) {
            balance = balance.subtract(amount);
            account.setBalance(balance);

            if (balance.compareTo(BigDecimal.valueOf(0.00)) < 0.00) {
                throw new InsufficientBalanceException();
            }
        }
        return account.getBalance();
    }
}