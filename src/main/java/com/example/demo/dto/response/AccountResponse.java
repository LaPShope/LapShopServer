package com.example.demo.dto.response;

import com.example.demo.common.Enums;
import com.example.demo.dto.CustomerDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    private Enums.Role role;

    private CustomerDTO customer;

    @JsonProperty("is_active")
    private boolean isActive;
}
