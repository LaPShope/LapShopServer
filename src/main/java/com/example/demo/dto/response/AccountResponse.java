package com.example.demo.dto.response;

import com.example.demo.common.Enums;
import com.example.demo.dto.CustomerDTO;
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

    private CustomerDTO customer;
}
