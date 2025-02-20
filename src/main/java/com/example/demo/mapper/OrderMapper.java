package com.example.demo.mapper;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.Models.Order;
import com.example.demo.Models.OrderDetail;
import com.example.demo.Models.Payment;

import java.util.Collections;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDTO convertToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .dateCreate(order.getDateCreate())
                .status(order.getStatus())
                .orderDetails(order.getOrderDetailList() == null
                        ? Collections.emptyList()
                        : order.getOrderDetailList().stream()
                        .map(OrderDetail::getId)
                        .collect(Collectors.toList()))
                .payments(order.getPaymentList() == null
                        ? Collections.emptyList()
                        : order.getPaymentList().stream()
                        .map(Payment::getId)
                        .collect(Collectors.toList()))
                .build();
    }

}
