package com.example.demo.DTO.Response;

import com.example.demo.DTO.AccountDTO;
import com.example.demo.DTO.CustomerDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
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
