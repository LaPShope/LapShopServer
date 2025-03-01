package com.example.demo.mapper;

import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.Response.CustomerResponse;
import com.example.demo.Models.*;

import java.util.Collections;
import java.util.stream.Collectors;

public class CustomerMapper {
    public static CustomerDTO convertToDTO(Customer customer) {
        return CustomerDTO.builder()
                .customerId(customer.getCustomerId().getId())
                .phone(customer.getPhone())
                .avatar(customer.getAvatar())
                .gender(customer.getGender())
                .bornDate(customer.getBornDate())
//                .addressIds(customer.getAddressList().stream()
//                        .map(Address::getId).collect(Collectors.toList()))
//                .paymentIds(customer.getPaymentList().stream()
//                        .map(Payment::getId).collect(Collectors.toList()))
//                .orderIds(customer.getOderList().stream()
//                        .map(Order::getId).collect(Collectors.toList()))
//                .cartIds(customer.getCartList().stream()
//                        .map(Cart::getId).collect(Collectors.toList()))
                .build();
    }

    public static CustomerResponse convertToResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId().getId())
                .phone(customer.getPhone())
                .avatar(customer.getAvatar())
                .gender(customer.getGender())
                .bornDate(customer.getBornDate())
                .addressList(customer.getAddressList() == null ? Collections.emptyList() :
                        customer.getAddressList().stream()
                        .map(AddressMapper::convertToDTO).collect(Collectors.toList()))
                .cartList(customer.getCartList() == null ? Collections.emptyList() :
                        customer.getCartList().stream()
                        .map(CartMapper::convertToDTO).collect(Collectors.toList()))
                .orderList(customer.getOderList() == null ? Collections.emptyList() :
                        customer.getOderList().stream()
                        .map(OrderMapper::convertToDTO).collect(Collectors.toList()))
                .paymentList(customer.getPaymentList() == null ? Collections.emptyList() :
                        customer.getPaymentList().stream()
                        .map(PaymentMapper::convertToDTO).collect(Collectors.toList()))
                .build();
    }
}
