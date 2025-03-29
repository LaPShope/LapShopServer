package com.example.demo.dto.response.order;

import com.example.demo.common.Enums;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.response.PaymentResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderResponse {
    private UUID id;

    private CustomerDTO customer;

    private Enums.OrderStatus status;

    @JsonProperty("date_create")
    private Date dateCreate;

    
    @JsonProperty("delivery_cost")
    private BigDecimal deliveryCost;

    @JsonProperty("final_price")
    private BigDecimal finalPrice;

    @JsonProperty("oder_details")
    private List<OrderItem> orderDetails;

    private PaymentResponse payments;
}
