package com.example.wallet.converter;

import com.example.wallet.model.Record;
import com.example.wallet.model.TransactionRequest;
import com.example.wallet.model.TransactionType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;


@AllArgsConstructor
@Component
public class ObjectConverter {
    public Record convertRequestObjectToRecord(TransactionRequest request, TransactionType type) {
        return Record.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .transactionId(UUID.randomUUID().toString())
                .transactionType(type)
                .build();
    }
}
