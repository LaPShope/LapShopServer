package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.request.auth.LoginRequest;
import com.example.demo.dto.request.auth.RegisterRequest;
import com.example.demo.dto.response.AccountResponse;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.auth.RegisterReponse;
import com.example.demo.model.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    public AccountResponse partialUpdateAccount(UUID id,Map<String,Object> fieldsToUpdate );
    public List<AccountResponse> getAllAccounts();
    public AccountResponse getAccount(UUID id);
    public Account getAccount(String email);
    public AccountResponse createAccount(AccountDTO accountDTO);
    public AccountResponse updateAccount(UUID id, AccountDTO updatedAccount);
    public void deleteAccount(UUID id);
    public LoginResponse login(LoginRequest loginRequest);
    public RegisterReponse register(RegisterRequest registerRequest);
    public void forgotPassword(String email);
}
