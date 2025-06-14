package com.example.demo.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

//ignore null values

@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
