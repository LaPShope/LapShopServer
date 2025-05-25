package com.example.demo.dto.response.cart;

import com.example.demo.dto.LaptopModelDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private UUID id;

    @JsonProperty("cart_id")
    private UUID cartId;

    @JsonProperty("laptop_model")
    private LaptopModelDTO laptopModel;

    private Integer quantity;
}
