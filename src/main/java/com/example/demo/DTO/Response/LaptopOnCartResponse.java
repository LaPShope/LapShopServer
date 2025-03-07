package com.example.demo.DTO.Response;

import com.example.demo.DTO.CartDTO;
import com.example.demo.DTO.LaptopModelDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LaptopOnCartResponse {
    private UUID id;
    private CartDTO cart;
    @JsonProperty("laptop_model")
    private LaptopModelDTO laptopModel;
    private Integer quantity;


}
