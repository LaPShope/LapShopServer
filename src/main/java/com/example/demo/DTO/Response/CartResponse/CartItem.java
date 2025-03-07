package com.example.demo.DTO.Response.CartResponse;

import com.example.demo.DTO.LaptopModelDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    private UUID id;

    @JsonProperty("cart_id")
    private UUID cartId;

    @JsonProperty("laptop_model")
    private LaptopModelDTO laptopModel;

    private Integer quantity;
}
