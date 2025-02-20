package com.example.demo.mapper;

import com.example.demo.DTO.PaymentDTO;
import com.example.demo.Models.Payment;

public class PaymentMapper {
    public static PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .customerId(payment.getCustomer() != null ? payment.getCustomer().getCustomerId().getId() : null)
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .paymentMethodId(payment.getPaymentMethod() != null ? payment.getPaymentMethod().getId() : null)
                .type(payment.getType())
                .status(payment.getStatus())
                .build();
    }
}
