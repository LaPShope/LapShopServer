package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerDTO {
    @JsonProperty("customer_id")
    private UUID customerId;

    private String gender;

    @JsonProperty("born_date")
    private Date bornDate;

    private String phone;

    private String avatar;
//
//    private AccountDTO account;
}