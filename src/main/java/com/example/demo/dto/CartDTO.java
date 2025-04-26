package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartDTO {
    private UUID id;

    // @JsonProperty("customer_id")
    // private UUID customerId;
    
    @JsonProperty("laptop_on_cart_id")
    private List<UUID> laptopOnCartIds;
}