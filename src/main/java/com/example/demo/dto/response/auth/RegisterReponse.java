package com.example.demo.dto.response.auth;

import com.example.demo.common.Enums;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class RegisterReponse {
    private UUID id;

    private String email;

    private String name;

    private String password;

    private Enums.Role role;

    private String token;

    @JsonProperty("is_active")
    private boolean isActive;
}
