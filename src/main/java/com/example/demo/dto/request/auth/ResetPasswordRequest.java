package com.example.demo.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

//ignore null values

@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    @JsonProperty("new_password")
    private String newPassword;
}
