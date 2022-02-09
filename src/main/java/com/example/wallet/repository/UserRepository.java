package com.example.wallet.repository;

import com.example.wallet.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("UserRepository")
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity getByUserId(String userId);

}