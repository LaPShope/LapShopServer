package com.example.demo.dto.response;

import com.example.demo.common.Enums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponse {
    private String email;
    
    private String name;
    
    // private String password;

    private Enums.Role role;
}
