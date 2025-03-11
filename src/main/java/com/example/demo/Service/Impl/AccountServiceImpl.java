package com.example.demo.Service.Impl;


import com.example.demo.Common.Enums;
import com.example.demo.DTO.AccountDTO;
import com.example.demo.DTO.Response.AccountResponse;
import com.example.demo.Models.Account;
import com.example.demo.Models.Admin;
import com.example.demo.Models.Customer;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Service.AccountService;


import com.example.demo.mapper.AccountMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    private final RedisService redisService;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AdminRepository adminRepository, RedisService redisService, AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.redisService = redisService;
        this.adminRepository = adminRepository;
    }

    // Lấy danh sách tài khoản
    @Transactional
    @Override
    public List<AccountResponse> getAllAccounts() {
        // kiem tra trong redis
        List<AccountResponse> cachedAccounts = redisService.getObject("allAccount", new TypeReference<List<AccountResponse>>() {});

        if (cachedAccounts != null && !cachedAccounts.isEmpty()) {
            return cachedAccounts;
        }
        //lay toan bo account
        List<AccountResponse> accounts = accountRepository.findAll().stream()
                .map(AccountMapper::convertToResponse)
                .collect(Collectors.toList());

        //Lưu vào Redis
        redisService.setObject("allAccount", accounts, 600);

        return accounts;
    }

    // Lấy chi tiết một tài khoản
    @Transactional
    @Override
    public AccountResponse getAccountById(UUID id) {
        AccountResponse cachedAccount = redisService.getObject("account:"+id, new TypeReference<AccountResponse>(){});

        if(cachedAccount != null){
            return cachedAccount;
        }

        Account accountExisting = accountRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        AccountResponse account =  AccountMapper.convertToResponse(accountExisting);

        redisService.setObject("account:"+id,account,600);

        return account;
    }

    // Tạo mới tài khoản
    @Transactional
    @Override
    public AccountResponse createAccount(AccountDTO accountDTO) {
        if (accountRepository.findByEmail(accountDTO.getEmail()).isPresent()) {
            throw new EntityExistsException("Email already exists!");
        }
        Account account = Account.builder()
                .id(null)
                .email(accountDTO.getEmail())
                .name(accountDTO.getName())
                .password(accountDTO.getPassword())
                .role(accountDTO.getRole())
                .build();

        if(account.getRole().equals(Enums.role.ADMIN)){
            Admin admin = new Admin();
            admin.setAdminId(account);
            account.setAdminId(admin);
        }

        Customer customer = new Customer();
        customer.setCustomerId(account);
        account.setCustomerId(customer);

        Account accountExisting = accountRepository.save(account);

        AccountResponse cachedAccount = AccountMapper.convertToResponse(accountExisting);

        redisService.setObject("account:"+accountExisting,cachedAccount,600);

        redisService.del("allAccount");

        return cachedAccount;
    }


    // Cập nhật thông tin tài khoản
    @Transactional
    @Override
    public AccountResponse updateAccount(UUID id, AccountDTO updatedAccount) {
        Account existingAccount = accountRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Optional<Account> existingAccount1 = accountRepository.findByEmail(updatedAccount.getEmail());

        if(existingAccount1.isPresent()){
            if(!existingAccount1.get().getId().equals(existingAccount.getId())){
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
        redisService.del("account:"+account.getId());

        redisService.setObject("account:"+account.getId(),cachedAccount,600);

        return cachedAccount;
    }

    @Transactional
    @Override
    public AccountResponse partialUpdateAccount(UUID id, Map<String, Object> fieldsToUpdate) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with ID " + id + " not found!"));

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
                            if(enumValue.equals(Enums.role.ADMIN)){
                                Admin admin = Admin.builder()
                                        .adminId(account)
                                        .build();
                                adminRepository.save(admin);
                            }else if(enumValue.equals(Enums.role.CUSTOMER)){
                                account.setAdminId(null);
                                adminRepository.deleteById(account.getId());
                            }
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid enum value for field: " + fieldName);
                        }
                    } else {
                        field.set(account, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException ("Unable to update field: " + fieldName, e);
            }
        }

        Account updatedAccount = accountRepository.save(account);
        AccountResponse cachedAccount = AccountMapper.convertToResponse(updatedAccount);

        redisService.del("allAccount");
        redisService.del("account"+updatedAccount.getId());

        redisService.setObject("account"+updatedAccount.getId(),cachedAccount,600);

        return cachedAccount;
    }


    // Xóa tài khoản
    @Transactional
    @Override
    public void deleteAccount(UUID id) {
        Account account =accountRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        redisService.del("allAccount");
        redisService.del("account:"+account.getId());

        accountRepository.deleteById(id);
    }

}
