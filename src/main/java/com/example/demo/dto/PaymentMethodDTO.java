package com.example.demo.dto;

import com.example.demo.common.Enums;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodDTO {
    private UUID id;
    private Map<String, Object> data;
    private Enums.PaymentType type;

}