package com.example.demo.mapper;

import com.example.demo.dto.PaymentDTO;
import com.example.demo.dto.response.PaymentResponse;
import com.example.demo.model.Payment;

public class PaymentMapper {
    public static PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
//                .customerId(payment.getCustomer() != null ? payment.getCustomer().getAccount().getId() : null)
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .paymentMethodId(payment.getPaymentMethod() != null ? payment.getPaymentMethod().getId() : null)
                .type(payment.getType())
                .status(payment.getStatus())
                .build();
    }

    public static PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .customer(payment.getCustomer() != null ? payment.getCustomer().getAccount().getId() : null)
                .order(payment.getOrder() != null ? OrderMapper.convertToDTO(payment.getOrder()) : null)
                .paymentMethod(payment.getPaymentMethod() != null ? PaymentMethodMapper.convertToDTO(payment.getPaymentMethod()) : null)
                .type(payment.getType())
                .status(payment.getStatus())
                .build();
    }
}
