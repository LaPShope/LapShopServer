package com.example.demo.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.demo.DTO.AccountDTO;
import com.example.demo.DTO.Response.AccountResponse;

public interface AccountService {
    
    public AccountResponse partialUpdateAccount(UUID id,Map<String,Object> fieldsToUpdate );
    public List<AccountResponse> getAllAccounts();
    public AccountResponse getAccountById(UUID id);
    public AccountResponse createAccount(AccountDTO accountDTO);
    public AccountResponse updateAccount(UUID id, AccountDTO updatedAccount);
    public void deleteAccount(UUID id);
}
