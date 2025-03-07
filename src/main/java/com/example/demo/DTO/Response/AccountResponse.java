package com.example.demo.DTO.Response;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.CustomerDTO;
import com.example.demo.Models.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private UUID id;

    private String email;

    private String name;

    private String password;

    private Enums.role role;

    private CustomerResponse customer;
}
