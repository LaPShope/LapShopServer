package com.example.demo.mapper;

import com.example.demo.DTO.CartDTO;
import com.example.demo.Models.Cart;
import com.example.demo.Models.LaptopOnCart;

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

}
