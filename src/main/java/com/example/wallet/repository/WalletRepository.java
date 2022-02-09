package com.example.wallet.repository;

import com.example.wallet.entity.WalletEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository("WalletRepository")
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    boolean existsByTransactionId(String transactionId);

}