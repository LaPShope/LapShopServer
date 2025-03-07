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
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
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

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Lấy danh sách tài khoản
    @Transactional
    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
            .map(AccountMapper::convertToResponse)
            .collect(Collectors.toList());
    }

    // Lấy chi tiết một tài khoản
    @Transactional
    @Override
    public AccountResponse getAccountById(UUID id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        return AccountMapper.convertToResponse(account);
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
        }else if(account.getRole().equals(Enums.role.CUSTOMER)){
            Customer customer = new Customer();
            customer.setCustomerId(account);
            account.setCustomerId(customer);
        }else{
            throw new IllegalArgumentException("Role Is Wrong");
        }

        Account accountExisting = accountRepository.save(account);

        return AccountMapper.convertToResponse(accountExisting);
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

        return AccountMapper.convertToResponse(account);
    }

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
        return AccountMapper.convertToResponse(updatedAccount);
    }


    // Xóa tài khoản
    @Transactional
    @Override
    public void deleteAccount(UUID id) {
        Account account =accountRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        accountRepository.deleteById(id);
    }

}
