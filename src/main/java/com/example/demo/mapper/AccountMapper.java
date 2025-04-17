package com.example.demo.mapper;

import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.response.AccountResponse;
import com.example.demo.model.Account;

public class AccountMapper {
    public static AccountDTO convertToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .email(account.getEmail())
                .password(account.getPassword())
                .name(account.getName())
                .role(account.getRole())
                .isActive(account.getIsActive())
                .build();
    }

    public static AccountResponse convertToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .password(account.getPassword())
                .name(account.getName())
                .role(account.getRole())
                .isActive(account.getIsActive())
                .customer(CustomerMapper.convertToDTO(account.getCustomer()))
                .build();
    }
}
