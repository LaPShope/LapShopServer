package com.example.demo.DTO;

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
    @JsonProperty("customer_id")
    private UUID customerId;
    private Integer quantity;
    @JsonProperty("laptop_on_cart_id")
    private List<UUID> laptopOnCartIds;
}