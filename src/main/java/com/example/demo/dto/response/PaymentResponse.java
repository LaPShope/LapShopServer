package com.example.demo.dto.response;

import com.example.demo.common.Enums;
import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.PaymentMethodDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentResponse {
    private UUID id;

    private UUID customer;

    @JsonProperty("order")
    private OrderDTO order;

    @JsonProperty("payment_method")
    private PaymentMethodDTO paymentMethod;

    private Enums.PaymentType type;

    private Enums.PaymentStatus status;
}
