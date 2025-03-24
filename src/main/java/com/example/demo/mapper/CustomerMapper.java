package com.example.demo.mapper;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.response.CustomerResponse;
import com.example.demo.model.*;

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
//                .account(AccountMapper.convertToDTO(customer.getCustomerId()))
                .build();
    }

    public static CustomerResponse convertToResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId().getId())
                .phone(customer.getPhone())
                .avatar(customer.getAvatar())
                .gender(customer.getGender())
                .bornDate(customer.getBornDate())
                .account(AccountMapper.convertToDTO(customer.getCustomerId()))
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
