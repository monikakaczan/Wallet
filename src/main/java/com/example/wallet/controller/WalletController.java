package com.example.wallet.controller;

import com.example.wallet.converter.ObjectConverter;
import com.example.wallet.entity.WalletEntity;
import com.example.wallet.model.TransactionRequest;
import com.example.wallet.model.TransactionType;
import com.example.wallet.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final ObjectConverter objectConverter;

    @PostMapping("/account/credit")
    public void processCredit(@RequestBody TransactionRequest credit) {
        final var record = objectConverter.convertRequestObjectToRecord(credit, TransactionType.CREDIT);

        walletService.processCredit(record);
    }

    @PostMapping("/account/debit")
    public void processDebit(@RequestBody TransactionRequest debit) {
        final var record = objectConverter.convertRequestObjectToRecord(debit, TransactionType.DEBIT);

        walletService.processDebit(record);
    }

    @GetMapping("account/{userId}/balance")
    @ResponseBody
    public BigDecimal getAccountBalance(@PathVariable("userId") String userId) {
        return walletService.getAccountBalance(userId);
    }

    @GetMapping("/account/{userId}/history")
    @ResponseBody
    public List<WalletEntity> getTransactionHistory(@PathVariable("userId") String userId) {
        return walletService.getTransactionHistory(userId);
    }
}
