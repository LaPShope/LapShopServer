package com.example.demo.DTO;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerDTO {
    private UUID customerId;
    private List<UUID> addressIds;
    private List<UUID> paymentIds;
    private List<UUID> orderIds;
    private List<UUID> cartIds;
}