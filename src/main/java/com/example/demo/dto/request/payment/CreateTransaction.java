package com.example.demo.dto.request.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class CreateTransaction {
    @JsonProperty("order_id")
    private UUID orderId;
}
