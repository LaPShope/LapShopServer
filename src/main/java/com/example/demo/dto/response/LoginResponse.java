package com.example.demo.dto.response;

import com.example.demo.common.Enums;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginResponse {
    private String token;
    private String email;
    private Enums.Role role;
    @JsonProperty("is_active")
    private boolean isActive;
}
