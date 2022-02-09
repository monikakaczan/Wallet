package com.example.wallet.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class Record {
    private BigDecimal amount;
    private String userId;
    private TransactionType transactionType;
    private String transactionId;
}

