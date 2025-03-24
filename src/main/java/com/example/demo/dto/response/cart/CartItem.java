package com.example.demo.dto.response.cart;

import com.example.demo.dto.LaptopModelDTO;
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
