package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    private UUID id;
    @JsonProperty("order_id")
    private UUID orderId;
    @JsonProperty("laptop_model_id")
    private UUID laptopModelId;
    private Integer quantity;
    private BigDecimal price;
}