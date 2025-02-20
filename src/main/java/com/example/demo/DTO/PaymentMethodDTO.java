package com.example.demo.DTO;

import com.example.demo.Common.Enums;
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
    private Enums.PaymentType type; // Tên type trong Enum

}