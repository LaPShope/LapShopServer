package com.example.demo.dto.response;

import com.example.demo.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private UUID id;
    private CustomerDTO customer;
    private String city;
    private String district;
    private String ward;
    private String street;
    private String phone;
}
