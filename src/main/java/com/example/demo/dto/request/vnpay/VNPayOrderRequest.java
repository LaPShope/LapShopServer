package com.example.demo.dto.request.vnpay;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
@NotNull
public class VNPayOrderRequest {
    private UUID orderId;
    private String orderInfo;
    private String ipAddr;
    private long amount;
}