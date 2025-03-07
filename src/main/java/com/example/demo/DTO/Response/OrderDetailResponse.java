package com.example.demo.DTO.Response;

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
public class OrderDetailResponse {
    private UUID id;

    private OrderDTO order;

    private LaptopModelDTO laptopModel;

    private Integer quantity;

    private BigDecimal price;
}
