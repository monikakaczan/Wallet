package com.example.wallet.unit;

import com.example.wallet.entity.UserEntity;
import com.example.wallet.exception.InvalidUserIdException;
import com.example.wallet.exception.TransactionAlreadyExistsException;
import com.example.wallet.model.Record;
import com.example.wallet.entity.WalletEntity;
import com.example.wallet.model.TransactionType;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.service.WalletService;
import com.example.wallet.utility.BalanceUtil;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    private WalletService walletService;
    private WalletRepository walletRepositoryMock;
    private BalanceUtil balanceUtilMock;
    private UserRepository userRepositoryMock;

    @Before
    public void setUp() {
        walletRepositoryMock = mock(WalletRepository.class);
        balanceUtilMock = mock(BalanceUtil.class);
        userRepositoryMock = mock(UserRepository.class);
        walletService = new WalletService(walletRepositoryMock, userRepositoryMock, balanceUtilMock);
    }

    @Test
    public void shouldProcessCredit() {
        // given
        final var transactionId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();

        final var walletEntity = WalletEntity.builder()
                .userId(userId)
                .amount(BigDecimal.valueOf(555.00))
                .transactionId(transactionId)
                .latestBalance(BigDecimal.valueOf(555.00))
                .transactionType(TransactionType.CREDIT)
                .build();

        final var record = Record.builder()
                .amount(BigDecimal.valueOf(555.00))
                .userId(userId)
                .transactionType(TransactionType.CREDIT)
                .transactionId(transactionId)
                .build();

        final var account = UserEntity.builder().userId(userId).balance(BigDecimal.valueOf(10)).build();
        when(userRepositoryMock.getByUserId(record.getUserId())).thenReturn(account);
        when(walletRepositoryMock.existsByTransactionId(transactionId)).thenReturn(false);

        // when
        walletService.processCredit(record);

        // then
        verify(balanceUtilMock, times(1)).calculateBalance(any(), any(), any(), any());
        verify(walletRepositoryMock, times(1)).save(any());
        verify(userRepositoryMock, times(2)).getByUserId(any());
        verify(walletRepositoryMock, times(1)).existsByTransactionId(any());
    }

    @Test
    public void shouldProcessDebit() {
        // given
        final var transactionId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();

        final var walletEntity = WalletEntity.builder()
                .userId(userId)
                .amount(BigDecimal.ONE)
                .transactionId(transactionId)
                .latestBalance(BigDecimal.TEN)
                .transactionType(TransactionType.DEBIT)
                .build();

        final var record = Record.builder()
                .amount(BigDecimal.ONE)
                .userId(userId)
                .transactionType(TransactionType.DEBIT)
                .transactionId(transactionId)
                .build();

        final var account = UserEntity.builder().userId(userId).build();
        when(userRepositoryMock.getByUserId(record.getUserId())).thenReturn(account);
        when(walletRepositoryMock.existsByTransactionId(transactionId)).thenReturn(false);

        // when
        walletService.processDebit(record);

        // then
        verify(balanceUtilMock, times(1)).calculateBalance(any(), any(), any(), any());
        verify(walletRepositoryMock, times(1)).save(any());
        verify(userRepositoryMock, times(2)).getByUserId(any());
        verify(walletRepositoryMock, times(1)).existsByTransactionId(any());
    }

    @Test
    public void shouldGetAccountBalance() {
        // given
        final var userId = "6c9f99c4-af9b-4522-abdf-66fcc50d4fbf";
        final var account = UserEntity.builder().userId(userId).balance(BigDecimal.valueOf(50.50)).build();

        when(userRepositoryMock.getByUserId(userId)).thenReturn(account);

        // when
        walletService.getAccountBalance(userId);

        // then
        verify(userRepositoryMock).getByUserId(userId);
        assertThat(walletService.getAccountBalance(userId)).isEqualTo(BigDecimal.valueOf(50.50));
    }

    @Test
    public void shouldGetTransactionHistoryPerUser() {
        // given
        final var userId = UUID.randomUUID().toString();

        final var transaction1 = WalletEntity.builder()
                .userId(userId)
                .amount(BigDecimal.valueOf(30.00))
                .transactionId("6c9f99c4-af9b-4522-abdf-66fcc50d4fb44444")
                .latestBalance(BigDecimal.valueOf(30.00))
                .transactionType(TransactionType.CREDIT)
                .build();

        final var transaction2 = WalletEntity.builder()
                .userId(userId)
                .amount(BigDecimal.valueOf(20.00))
                .transactionId("6c9f99c4-af9b-4524fb444")
                .latestBalance(BigDecimal.valueOf(50.00))
                .transactionType(TransactionType.CREDIT)
                .build();

        List<WalletEntity> transactions = Arrays.asList(transaction1, transaction2);
        final var account = UserEntity.builder()
                .userId(userId)
                .balance(transaction2.getLatestBalance())
                .transactions(transactions)
                .build();

        walletRepositoryMock.save(transaction1);
        walletRepositoryMock.save(transaction2);

        when(userRepositoryMock.getByUserId(userId)).thenReturn(account);

        // when
        walletService.getTransactionHistory(userId);

        // then
        verify(userRepositoryMock).getByUserId(userId);
        assertThat(walletService.getTransactionHistory(userId)).hasSize(2);
    }

    @Test
    public void shouldNotAcceptTransactionsWithAlreadyExistingId_OnCredit() {
        // given
        final var userId = UUID.randomUUID().toString();

        final var record = Record.builder()
                .amount(BigDecimal.ONE)
                .userId(userId)
                .transactionType(TransactionType.CREDIT)
                .transactionId("transactionId")
                .build();

        final var record2 = Record.builder()
                .amount(BigDecimal.ONE)
                .userId(userId)
                .transactionType(TransactionType.CREDIT)
                .transactionId("transactionId")
                .build();
        when(walletRepositoryMock.existsByTransactionId(record.getTransactionId())).thenReturn(true);

        // when
        final var thrown =
                catchThrowableOfType(
                        () -> walletService.processCredit(record2),
                        TransactionAlreadyExistsException.class);

        // then
        verify(walletRepositoryMock).existsByTransactionId(record.getTransactionId());
        assertThat(thrown).isExactlyInstanceOf(TransactionAlreadyExistsException.class);
        verify(walletRepositoryMock, never()).save(any());
    }

    @Test
    public void shouldNotAcceptTransactionsWithAlreadyExistingId_OnDebit() {
        // given
        final var userId = UUID.randomUUID().toString();

        final var record = Record.builder()
                .amount(BigDecimal.ONE)
                .userId(userId)
                .transactionType(TransactionType.DEBIT)
                .transactionId("transactionId")
                .build();

        final var record2 = Record.builder()
                .amount(BigDecimal.ONE)
                .userId(userId)
                .transactionType(TransactionType.DEBIT)
                .transactionId("transactionId")
                .build();

        when(walletRepositoryMock.existsByTransactionId(record.getTransactionId())).thenReturn(true);

        // when
        final var thrown =
                catchThrowableOfType(
                        () -> walletService.processDebit(record2),
                        TransactionAlreadyExistsException.class);

        // then
        verify(walletRepositoryMock).existsByTransactionId(record.getTransactionId());
        assertThat(thrown).isExactlyInstanceOf(TransactionAlreadyExistsException.class);
        verify(walletRepositoryMock, never()).save(any());
    }
}
