package com.example.demo.mapper;

import com.example.demo.dto.PaymentMethodDTO;
import com.example.demo.model.PaymentMethod;

public class PaymentMethodMapper {
    public static PaymentMethodDTO convertToDTO(PaymentMethod paymentMethod) {
        return PaymentMethodDTO.builder()
                .id(paymentMethod.getId())
                .type(paymentMethod.getType())
                .data(paymentMethod.getData())
                .build();
    }
}
