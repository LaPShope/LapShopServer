package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.common.Enums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private UUID id;

    private String email;

    private String name;

    private String password;

    private Enums.Role role;

}
