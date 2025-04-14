package com.example.demo.service.impl;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.AuthUtil;
import com.example.demo.common.Enums;
import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.response.AccountResponse;
import com.example.demo.dto.response.AdminResponse;
import com.example.demo.mapper.AccountMapper;
import com.example.demo.model.Account;
import com.example.demo.model.Admin;
import com.example.demo.model.Customer;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.AdminRepository;
import com.example.demo.service.AdminService;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;


@Service
public class AdminServiceImpl implements AdminService {

    private final AccountRepository accountRepository;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder, AccountRepository accountRepository, RedisService redisService) {
            this.accountRepository = accountRepository;
            this.redisService = redisService;
            this.passwordEncoder=passwordEncoder;
            this.adminRepository=adminRepository;
        }

    @Override
    public AdminResponse createAdmin(AccountDTO accountDTO) {
        // if(!AuthUtil.isAdmin()){
        //     throw new SecurityException("User is not an Admin");
        // }

        Account account = Account.builder()
                .id(null)
                .email(accountDTO.getEmail())
                .name(accountDTO.getName())
                .password(passwordEncoder.encode(accountDTO.getPassword()))
                .role(Enums.Role.Admin)
                .build();

        Admin admin = new Admin();
        Customer customer = new Customer();

        admin.setAccount(account);       
        customer.setAccount(account); 
        
        account.setCustomer(customer);
        account.setAdmin(admin);

        Account accountExisting = accountRepository.save(account);

        AdminResponse adminResponse = AdminResponse.builder()
                            .email(accountDTO.getEmail())
                            .name(account.getName())
                            .role(account.getRole())
                            .build();

        redisService.setObject("account:" + accountExisting, adminResponse, 600);

        redisService.del("allAccount");

        return adminResponse;
    }

    // Cập nhật thông tin tài khoản
    @Transactional
    @Override
    public AccountResponse updateAccount(UUID id, AccountDTO updatedAccount) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        Optional<Account> existingAccount1 = accountRepository.findByEmail(updatedAccount.getEmail());

        if (existingAccount1.isPresent()) {
            if (!existingAccount1.get().getEmail().equals(existingAccount.getEmail())) {
                throw new EntityExistsException("Email already existed");
            }
        }

        existingAccount.setName(updatedAccount.getName());
        existingAccount.setEmail(updatedAccount.getEmail());
        existingAccount.setRole(updatedAccount.getRole());
        existingAccount.setPassword(updatedAccount.getPassword());

        Account account = accountRepository.save(existingAccount);

        AccountResponse cachedAccount = AccountMapper.convertToResponse(account);

        redisService.del("allAccount");
        redisService.del("account:" + account.getId());

        redisService.setObject("account:" + account.getId(), cachedAccount, 600);

        return cachedAccount;
    }

    @Transactional
    @Override
    public AccountResponse partialUpdateAccount(UUID id, Map<String, Object> fieldsToUpdate) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with ID " + id + " not found!"));

        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        Class<?> clazz = account.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {

                    if (field.getType().isEnum()) {
                        try {
                            Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), newValue.toString());
                            field.set(account, enumValue);
                            if (enumValue.equals(Enums.Role.Admin)) {
                                Admin admin = Admin.builder()
                                        .account(account)
                                        .build();
                                account.setAdmin(admin);
                                // adminRepository.save(admin);
                            } else if (enumValue.equals(Enums.Role.Customer)) {
                                adminRepository.deleteById(account.getId());
                            }
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid enum value for field: " + fieldName);
                        }
                    }else if("email".equals(fieldName)){
                        String newEmail = newValue.toString();
                        accountRepository.existsByEmail(newEmail).orElseThrow(() -> new EntityExistsException("Email already exist"));
                        field.set(account, newEmail);
                    }else {
                        field.set(account, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Account updatedAccount = accountRepository.save(account);
        AccountResponse cachedAccount = AccountMapper.convertToResponse(updatedAccount);

        redisService.del("allAccount");
        redisService.del("account" + updatedAccount.getId());

        redisService.setObject("account" + updatedAccount.getId(), cachedAccount, 600);

        return cachedAccount;
    }
    
}
