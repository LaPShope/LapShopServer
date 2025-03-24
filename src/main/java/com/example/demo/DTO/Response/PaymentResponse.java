package com.example.demo.DTO.Response;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.PaymentMethodDTO;
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
