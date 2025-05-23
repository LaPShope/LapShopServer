package com.example.demo.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @JsonProperty("old_password")
    private String oldPassword;
    @JsonProperty("new_password")
    private String newPassword;
}
