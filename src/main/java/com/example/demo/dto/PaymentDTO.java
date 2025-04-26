package com.example.demo.dto;

import com.example.demo.common.Enums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private UUID id;

    // @JsonProperty("customer_id")
    // private UUID customerId;

    @JsonProperty("order_id")
    private UUID orderId;

    @JsonProperty("payment_method_id")
    private UUID paymentMethodId;

    private Enums.PaymentType type;

    private Enums.PaymentStatus status;
}