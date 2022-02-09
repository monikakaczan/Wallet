package com.example.wallet.integration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.example.wallet.WalletApplication;
import com.example.wallet.controller.WalletController;
import com.example.wallet.entity.UserEntity;
import com.example.wallet.entity.WalletEntity;
import com.example.wallet.exception.InsufficientBalanceException;
import com.example.wallet.model.TransactionRequest;
import com.example.wallet.model.TransactionType;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WalletApplication.class)
public class WalletIntegrationTest {

    @Autowired
    private WalletRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletController walletController;

    @Test
    public void shouldProcessCredit() {
        // given
        final var userId = UUID.randomUUID().toString();

        final var transaction = TransactionRequest.builder()
                .amount(BigDecimal.ONE)
                .userId(userId)
                .build();

        final var walletEntity = WalletEntity.builder()
                .userId(userId)
                .amount(BigDecimal.ONE)
                .transactionId(UUID.randomUUID().toString())
                .latestBalance(BigDecimal.ONE)
                .createdAt(Timestamp.from(Instant.now()))
                .transactionType(TransactionType.CREDIT)
                .build();

        seedUserTable(userId, BigDecimal.ZERO);
        repository.save(walletEntity);

        // when
        walletController.processCredit(transaction);

        // then
        final var entities = walletController.getTransactionHistory(userId);
        assertThat(entities.size()).isEqualTo(2);
    }

    @Test
    public void shouldProcessDebit() {
        // given
        final var userId = UUID.randomUUID().toString();

        final var transaction = TransactionRequest.builder()
                .amount(BigDecimal.ONE)
                .userId(userId)
                .build();

        final var walletEntity = WalletEntity.builder()
                .userId(userId)
                .amount(BigDecimal.ONE)
                .transactionId(UUID.randomUUID().toString())
                .latestBalance(BigDecimal.valueOf(499.00))
                .transactionType(TransactionType.DEBIT)
                .build();

        seedUserTable(userId, BigDecimal.valueOf(500.00));
        repository.save(walletEntity);

        // when
        walletController.processDebit(transaction);

        // then
        final var entities = walletController.getTransactionHistory(userId);
        assertThat(entities.size()).isEqualTo(2);
    }

    @Test
    public void shouldGetAccountBalance() {
        // given
        final var userId = UUID.randomUUID().toString();
        seedUserTable(userId, BigDecimal.valueOf(500.00));

        // when
        final var balance = walletController.getAccountBalance(userId);

        // then
        assertThat(balance).isEqualTo(BigDecimal.valueOf(500.00));
    }

    @Test
    public void shouldGetAccountTransactionHistory() {
        // given
        final var userId = UUID.randomUUID().toString();

        final var walletEntity = WalletEntity.builder()
                .userId(userId)
                .amount(BigDecimal.ONE)
                .transactionId(UUID.randomUUID().toString())
                .latestBalance(BigDecimal.ONE)
                .transactionType(TransactionType.CREDIT)
                .build();

        final var walletEntity2 = WalletEntity.builder()
                .userId(userId)
                .amount(BigDecimal.ONE)
                .transactionId(UUID.randomUUID().toString())
                .latestBalance(BigDecimal.valueOf(2))
                .transactionType(TransactionType.CREDIT)
                .build();

        seedUserTable(userId, BigDecimal.ZERO);
        List<WalletEntity> transactions = Arrays.asList(walletEntity, walletEntity2);

        repository.save(walletEntity);
        repository.save(walletEntity2);

        final var user = userRepository.getByUserId(userId);

        // when
        final var history = walletController.getTransactionHistory(userId);

        // then
        assertThat(history).hasSize(2);
        assertThat(transactions).isEqualTo(user.getTransactions());
    }

    @Test
    public void shouldThrowInsufficientBalanceException() {
        // given
        final var userId = UUID.randomUUID().toString();

        final var transactionRequest = TransactionRequest.builder()
                .userId(userId)
                .amount(BigDecimal.valueOf(500.00))
                .build();

        seedUserTable(userId, BigDecimal.ZERO);

        //when
        final var thrown =
              catchThrowableOfType(
                      () -> walletController.processDebit(transactionRequest),
                      InsufficientBalanceException.class);

        // then
        assertThat(thrown).isExactlyInstanceOf(InsufficientBalanceException.class);
    }

    private void seedUserTable(String userId, BigDecimal balance) {
        final var userEntity = UserEntity.builder().userId(userId).balance(balance).build();
        userRepository.save(userEntity);
    }
}

