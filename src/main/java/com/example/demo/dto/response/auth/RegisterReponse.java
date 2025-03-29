package com.example.demo.dto.response.auth;

import com.example.demo.common.Enums;
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

    private Enums.role role;

    private String token;
}
