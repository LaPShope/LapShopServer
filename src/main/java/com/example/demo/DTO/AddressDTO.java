package com.example.demo.DTO;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private UUID id;
    @JsonProperty("customer_id")
    private UUID customerId;
    private String city;
    private String district;
    private String ward;
    private String street;
    private String phone;
}
