package com.example.demo.DTO.Response.OrderResponse;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.OrderDetailDTO;
import com.example.demo.DTO.Response.PaymentResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderResponse {
    private UUID id;

    private CustomerDTO customer;

    private Enums.OrderStatus status;

    @JsonProperty("date_create")
    private Date dateCreate;

    @JsonProperty("oder_details")
    private List<OrderItem> orderDetails;

    private PaymentResponse payments;
}
