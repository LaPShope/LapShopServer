package com.example.demo.mapper;

import com.example.demo.DTO.OrderDetailDTO;
import com.example.demo.Models.OrderDetail;

public class OrderDetailMapper {
    public static OrderDetailDTO convertToDTO(OrderDetail orderDetail) {
        return OrderDetailDTO.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getId())
                .laptopModelId(orderDetail.getLaptopModel().getId())
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .build();
    }
}
