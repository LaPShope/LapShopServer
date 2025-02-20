package com.example.demo.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.demo.DTO.AccountDTO;

public interface AccountService {
    
    public AccountDTO partialUpdateAccount(UUID id,Map<String,Object> fieldsToUpdate );
    public List<AccountDTO> getAllAccounts();
    public AccountDTO getAccountById(UUID id);
    public AccountDTO createAccount(AccountDTO accountDTO);
    public AccountDTO updateAccount(UUID id, AccountDTO updatedAccount);
    public void deleteAccount(UUID id);
}
