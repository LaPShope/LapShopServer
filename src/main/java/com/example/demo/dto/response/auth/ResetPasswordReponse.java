package com.example.demo.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResetPasswordReponse {
    private String email;
    @JsonProperty("jwt_token")
    private String jwtToken;
    @JsonProperty("new_password")
    private String newPassword;
}
