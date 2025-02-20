package com.example.demo.DTO;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartDTO {
    private UUID id;
    private UUID customerId;
    private List<UUID> laptopOnCartIds;
}