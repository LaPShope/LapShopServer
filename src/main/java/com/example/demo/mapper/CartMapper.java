package com.example.demo.mapper;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.response.cart.CartResponse;
import com.example.demo.model.Cart;
import com.example.demo.model.LaptopOnCart;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class CartMapper {
    public static CartDTO convertToDTO(Cart cart) {
        return CartDTO.builder()
                .id(cart.getId())
                .customerId(cart.getCustomer().getId())
                .laptopOnCartIds(Optional.ofNullable(cart.getLaptopOnCarts())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(LaptopOnCart::getId)
                        .collect(Collectors.toList()))
                .build();

    }

    public static CartResponse convertToResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .laptopOnCartList(cart.getLaptopOnCarts() == null ? Collections.emptyList() :
                        cart.getLaptopOnCarts().stream()
                        .map(LaptopOnCartMapper::convertToItem)
                                .collect(Collectors.toList()))
                .customer(CustomerMapper.convertToDTO(cart.getCustomer()))
                .build();

    }

}
