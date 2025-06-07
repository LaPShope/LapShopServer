package com.example.demo.mapper;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.response.order.OrderResponse;
import com.example.demo.model.Order;

import java.util.Collections;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDTO convertToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getAccount().getId())
                .dateCreate(order.getDateCreate())
                .status(order.getStatus())
                .finalPrice(order.getFinalPrice())
                .deliveryCost(order.getDeliveryCost())
//                .orderDetails(order.getOrderDetailList() == null
//                        ? Collections.emptyList()
//                        : order.getOrderDetailList().stream()
//                        .map(OrderDetail::getId)
//                        .collect(Collectors.toList()))
//                .payments(order.getPaymentList() == null
//                        ? Collections.emptyList()
//                        : order.getPaymentList().stream()
//                        .map(Payment::getId)
//                        .collect(Collectors.toList()))
                .build();
    }

    public static OrderResponse convertToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customer(CustomerMapper.convertToDTO(order.getCustomer()))
                .dateCreate(order.getDateCreate())
                .address(order.getAddress())
                .status(order.getStatus())
                .finalPrice(order.getFinalPrice())
                .deliveryCost(order.getDeliveryCost())
                .orderDetails(order.getOrderDetailList() == null
                        ? Collections.emptyList()
                        : order.getOrderDetailList().stream()
                        .map(OrderDetailMapper::convertToItem)
                        .collect(Collectors.toList()))
                .payments(order.getPayment() == null
                        ? null
                        : PaymentMapper.convertToResponse(order.getPayment()))
                .build();
    }
}
