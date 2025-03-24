package com.example.demo.DTO.Response.CartResponse;

import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.Response.LaptopOnCartResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartResponse {
    private UUID id;

    private Integer quantity;

    private CustomerDTO customer;

    @JsonProperty("laptop_on_cart_list")
    private List<CartItem> laptopOnCartList;
}
