package com.example.wallet.service;

import com.example.wallet.entity.UserEntity;
import com.example.wallet.entity.WalletEntity;
import com.example.wallet.exception.InvalidUserIdException;
import com.example.wallet.exception.TransactionAlreadyExistsException;
import com.example.wallet.model.Record;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.utility.BalanceUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;


@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final BalanceUtil balanceUtil;

    public void processCredit(Record record) {
        validateIfTransactionExists(record.getTransactionId());
        processTransaction(record);
    }

    public void processDebit(Record record) {
        validateIfTransactionExists(record.getTransactionId());
        processTransaction(record);
    }

    public BigDecimal getAccountBalance(String userId) {
        final var user = userRepository.getByUserId(userId);
        return user.getBalance();
    }

    public List<WalletEntity> getTransactionHistory(String userId) {
        final var user = userRepository.getByUserId(userId);
        return user.getTransactions();
    }

    private void processTransaction(Record record) {
        final var account = getAccountOrThrowException(record.getUserId());
        final var balance = balanceUtil.calculateBalance(record.getAmount(), getAccountBalance(account.getUserId()), record.getTransactionType(), account);

        final var entity = WalletEntity.builder()
                .amount(record.getAmount())
                .userId(account.getUserId())
                .transactionId(record.getTransactionId())
                .latestBalance(balance)
                .createdAt(Timestamp.from(Instant.now()))
                .transactionType(record.getTransactionType())
                .build();
        walletRepository.save(entity);
    }

    private UserEntity getAccountOrThrowException(String userId) {
        final var account = userRepository.getByUserId(userId);

        if (account == null) {
            throw new InvalidUserIdException();
        }
        return account;
    }

    private void validateIfTransactionExists(String transactionId) {
        if (walletRepository.existsByTransactionId(transactionId)) {
            throw new TransactionAlreadyExistsException();
        }
    }
}