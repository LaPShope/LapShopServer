package com.example.demo.dto.request.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.SecondaryRow;

import java.util.UUID;

@Builder
@Getter
@SecondaryRow
public class DeleteLaptopOnCartResponse {
    @JsonProperty("cart_id")
    private UUID cartId;
    @JsonProperty("laptop_model_id")
    private UUID laptopModelId;
}