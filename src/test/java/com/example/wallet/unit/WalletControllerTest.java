package com.example.wallet.unit;

import com.example.wallet.controller.WalletController;
import com.example.wallet.entity.WalletEntity;
import com.example.wallet.model.TransactionType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WalletController walletControllerMock;

    @Test
    public void shouldProcessCredit() throws Exception {
        mockMvc.perform(post("/account/credit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"amount\":\"5.12\", \"userId\":\"f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454\", \"transactionId\":\"f718248f-854a-4b21-a14f-08d186292b52\"}"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldProcessDebit() throws Exception {
        mockMvc.perform(post("/account/debit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"amount\":\"10.00\", \"userId\":\"f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454\", \"transactionId\":\"f718248f-854a-4b21-a14f-08d186292b54\"}"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldReturnBalancePerPlayer() throws Exception {

        given(walletControllerMock.getAccountBalance("userId"))
                .willReturn(BigDecimal.valueOf(1.25));

        mockMvc.perform(get("/account/userId/balance")
                        .param("userId", "userId")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1.25"));
    }

    @Test
    public void shouldReturnTransactionHistoryPerPlayer() throws Exception {

        final var walletEntity = WalletEntity.builder()
                .userId("userId")
                .amount(BigDecimal.valueOf(1.00))
                .transactionId("f8c2de3d-1fea-4d7c-a8b0-29f63c444452")
                .transactionType(TransactionType.CREDIT)
                .build();

        final var walletEntity2 = WalletEntity.builder()
                .userId("userId")
                .amount(BigDecimal.valueOf(1.00))
                .transactionId("f8c2de3d-1fea-4d7c-a8b0-29f63c4444555")
                .transactionType(TransactionType.DEBIT)
                .build();

        List<WalletEntity> response = Arrays.asList(walletEntity, walletEntity2);

        given(walletControllerMock.getTransactionHistory("userId"))
                .willReturn(response);

        mockMvc.perform(get("/account/userId/history")
                        .param("userId", "userId")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].amount", hasSize(2)))
                .andExpect(jsonPath("$[*].transactionType", containsInAnyOrder("CREDIT", "DEBIT")))
                .andReturn();
    }
}
