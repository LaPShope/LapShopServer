package com.example.demo.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    private UUID id;
    private UUID orderId;
    private UUID laptopModelId;
    private Integer quantity;
    private BigDecimal price;
}