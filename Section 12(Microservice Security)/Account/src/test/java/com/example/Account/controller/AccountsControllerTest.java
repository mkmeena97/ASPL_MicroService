package com.example.Account.controller;

import com.example.Account.controllers.AccountsController;
import com.example.Account.dto.*;
import com.example.Account.service.IAccountsService;
import com.example.Account.service.ITransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountsController.class)
class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAccountsService accountsService;

    @MockBean
    private ITransactionService transactionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createAccount_shouldReturnCreated() throws Exception {
        CustomerDto dto = new CustomerDto("John Doe", "john@example.com", "9876543210", "SAVINGS", "Main Branch");

        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(accountsService).createAccount(any(CustomerDto.class));
    }

    @Test
    void addMoney_shouldReturnOk() throws Exception {
        AddMoneyRequestDto dto = new AddMoneyRequestDto("9876543210", new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/add-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(accountsService).addMoney(any(AddMoneyRequestDto.class));
    }

    @Test
    void transferMoney_shouldReturnOk() throws Exception {
        TransferMoneyRequestDto dto = new TransferMoneyRequestDto(
                "9876543210", "9123456789", new BigDecimal("500.00"));

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(accountsService).transferMoney(any(TransferMoneyRequestDto.class));
    }

    @Test
    void getAccountBalance_shouldReturnBalance() throws Exception {
        AccountBalanceDto balanceDto = new AccountBalanceDto("42", "9876543210", BigDecimal.valueOf(4200), "SAVINGS", "ACTIVE");
        when(accountsService.getBalance(eq("9876543210"))).thenReturn(balanceDto);

        mockMvc.perform(get("/api/balance")
                        .param("mobileNumber", "9876543210"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("42"))
                .andExpect(jsonPath("$.balance").value(4200));
    }

    @Test
    void getMiniStatement_shouldReturnList() throws Exception {
        TransactionDto tx1 = new TransactionDto(1, 42, "CREDIT", new BigDecimal("500"), "Deposit", null, null);
        TransactionDto tx2 = new TransactionDto(2, 42, "DEBIT", new BigDecimal("200"), "ATM Withdraw", null, null);
        when(transactionService.getMiniStatement(eq("9876543210"), eq(2))).thenReturn(List.of(tx1, tx2));

        mockMvc.perform(get("/api/mini-statement")
                        .param("mobileNumber", "9876543210")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(1))
                .andExpect(jsonPath("$[1].transactionId").value(2));
    }

    @Test
    void freezeUnfreeze_shouldReturnOk() throws Exception {
        FreezeUnfreezeRequestDto dto = new FreezeUnfreezeRequestDto("9876543210", FreezeUnfreezeRequestDto.Action.FREEZE);

        mockMvc.perform(put("/api/freeze-unfreeze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(accountsService).freezeOrUnfreeze(any(FreezeUnfreezeRequestDto.class));
    }

    @Test
    void changeAccountType_shouldReturnOk() throws Exception {
        ChangeAccountTypeRequestDto dto = new ChangeAccountTypeRequestDto("9876543210", "CURRENT");

        mockMvc.perform(put("/api/change-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(accountsService).changeAccountType(any(ChangeAccountTypeRequestDto.class));
    }

    // Add more tests as needed for fetch, update, delete, etc.
}
