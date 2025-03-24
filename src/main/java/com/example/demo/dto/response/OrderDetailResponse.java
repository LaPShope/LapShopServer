package com.example.demo.dto.response;

import com.example.demo.dto.LaptopModelDTO;
import com.example.demo.dto.OrderDTO;
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
