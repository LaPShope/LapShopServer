package com.example.demo.dto.response.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChangePasswordResponse {
    private String email;
    private String token;

}
