package com.example.demo.dto.request.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddLaptopToCart {
    @JsonProperty("customer_id")
    UUID customerId;
    @JsonProperty("laptop_model_id")
    UUID laptopModelId;
    Integer quantity;
}
