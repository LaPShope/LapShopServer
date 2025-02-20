package com.example.demo.DTO;

import com.example.demo.Common.Enums;
import com.example.demo.Models.Customer;
import com.example.demo.Models.Order;
import com.example.demo.Models.Payment;
import com.example.demo.Models.PaymentMethod;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private UUID id;
    private UUID customerId;
    private UUID orderId;
    private UUID paymentMethodId;
    private Enums.PaymentType type;
    private Enums.PaymentStatus status;
}