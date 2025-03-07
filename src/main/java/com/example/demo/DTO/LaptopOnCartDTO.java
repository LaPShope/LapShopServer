package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LaptopOnCartDTO {
    private UUID id;
    @JsonProperty("cart_id")
    private UUID cartId;
    @JsonProperty("laptop_model_id")
    private UUID laptopModelId;
    private Integer quantity;
}
