package com.example.demo.mapper;

import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.dto.response.OrderDetailResponse;
import com.example.demo.dto.response.order.OrderItem;
import com.example.demo.model.OrderDetail;

public class OrderDetailMapper {
    public static OrderDetailDTO convertToDTO(OrderDetail orderDetail) {
        return OrderDetailDTO.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .laptopModelId(orderDetail.getLaptopModel().getId())
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .build();
    }

    public static OrderDetailResponse convertToResponse(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .order(OrderMapper.convertToDTO(orderDetail.getOrder()))
                .laptopModel(LaptopModelMapper.convertToDTO(orderDetail.getLaptopModel()))
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .build();
    }

    public static OrderItem convertToItem(OrderDetail orderDetail) {
        return OrderItem.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .laptopModel(LaptopModelMapper.convertToDTO(orderDetail.getLaptopModel()))
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .build();
    }
}
