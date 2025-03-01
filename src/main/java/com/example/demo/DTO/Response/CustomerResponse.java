package com.example.demo.DTO.Response;

import com.example.demo.DTO.AddressDTO;
import com.example.demo.DTO.CartDTO;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.PaymentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    @JsonProperty("customer_id")
    private UUID customerId;

    private String gender;

    @JsonProperty("born_date")
    private Date bornDate;

    private String phone;

    private String avatar;

    @JsonProperty("address_list")
    private List<AddressDTO> addressList;

    @JsonProperty("payment_list")
    private List<PaymentDTO> paymentList;

    @JsonProperty("order_list")
    private List<OrderDTO> orderList;

    @JsonProperty("cart_list")
    private List<CartDTO> cartList;
}
