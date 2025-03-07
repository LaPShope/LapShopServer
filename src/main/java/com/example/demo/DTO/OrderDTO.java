package com.example.demo.DTO;

import com.example.demo.Common.Enums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private UUID id;

    @JsonProperty("customer_id")
    private UUID customerId;

    private Enums.OrderStatus status;

    @JsonProperty("date_create")
    private Date dateCreate;

    @JsonProperty("oder_details")
    private List<UUID> orderDetails;

    private List<UUID> payments;
}