package com.example.demo.dto.request.vnpay;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@NotNull
public class VNPayOrderRequest {
    private String orderInfo;
    private String ipAddr;
    private long amount;
}