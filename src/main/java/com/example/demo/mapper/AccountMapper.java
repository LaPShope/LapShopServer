package com.example.demo.mapper;

import com.example.demo.DTO.AccountDTO;
import com.example.demo.Models.Account;

public class AccountMapper {
    public static AccountDTO convertToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .email(account.getEmail())
                .password(account.getPassword())
                .name(account.getName())
                .role(account.getRole())
                .build();
    }
}
