package com.example.demo.service.impl;

import org.springframework.stereotype.Service;

import com.example.demo.common.Enums;
import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.response.AdminResponse;
import com.example.demo.model.Account;
import com.example.demo.model.Admin;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AdminService;


@Service
public class AdminServiceImpl implements AdminService {

    private final AccountRepository accountRepository;
    private final RedisService redisService;

    public AdminServiceImpl(AccountRepository accountRepository, RedisService redisService) {
            this.accountRepository = accountRepository;
            this.redisService = redisService;
        }

    @Override
    public AdminResponse createAdmin(AccountDTO accountDTO) {
        Account account = Account.builder()
                .id(null)
                .email(accountDTO.getEmail())
                .name(accountDTO.getName())
                .password(accountDTO.getPassword())
                .role(Enums.Role.Admin)
                .build();

        Admin admin = new Admin();
        admin.setAccount(account);

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
