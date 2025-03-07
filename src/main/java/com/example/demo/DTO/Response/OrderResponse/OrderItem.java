package com.example.demo.DTO.Response.OrderResponse;

import com.example.demo.DTO.LaptopModelDTO;
import com.example.demo.DTO.OrderDTO;
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
