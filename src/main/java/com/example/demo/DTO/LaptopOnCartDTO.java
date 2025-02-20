package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LaptopOnCartDTO {
    private UUID id;
    private UUID cartId;
    private UUID laptopModelId;
    private Integer quantity;
}
