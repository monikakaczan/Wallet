package com.example.wallet.entity;

import com.example.wallet.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JoinColumn(name="user_id", nullable=false)
    private String userId;

    @Column(nullable = false)
    private BigDecimal amount;

    private String transactionId;

    @Column(name="balance")
    private BigDecimal latestBalance;

    private TransactionType transactionType;

    private Timestamp createdAt;

}
