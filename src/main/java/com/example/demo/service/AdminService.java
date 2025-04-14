package com.example.demo.service;

import java.util.Map;
import java.util.UUID;

import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.response.AccountResponse;
import com.example.demo.dto.response.AdminResponse;

public interface AdminService {
    public AdminResponse createAdmin (AccountDTO accountDTO);
    public AccountResponse partialUpdateAccount(UUID id,Map<String,Object> fieldsToUpdate );
    public AccountResponse updateAccount(UUID id, AccountDTO updatedAccount);
}
