package com.example.demo.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.common.AuthUtil;
import com.example.demo.common.Enums;
import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.response.AdminResponse;
import com.example.demo.model.Account;
import com.example.demo.model.Admin;
import com.example.demo.model.Customer;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AdminService;


@Service
public class AdminServiceImpl implements AdminService {

    private final AccountRepository accountRepository;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(PasswordEncoder passwordEncoder, AccountRepository accountRepository, RedisService redisService) {
            this.accountRepository = accountRepository;
            this.redisService = redisService;
            this.passwordEncoder=passwordEncoder;
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
}
