package com.example.demo.dto.response.order;

import com.example.demo.dto.LaptopModelDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {
    private UUID id;

    @JsonProperty("order_id")
    private UUID orderId;

    private LaptopModelDTO laptopModel;

    private Integer quantity;

    private BigDecimal price;
}
