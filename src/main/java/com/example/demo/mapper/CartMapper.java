package com.example.demo.mapper;

import com.example.demo.dto.request.cart.CartDTO;
import com.example.demo.dto.response.cart.CartResponse;
import com.example.demo.model.Cart;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class CartMapper {
    public static CartDTO convertToDTO(Cart cart) {
        return CartDTO.builder()
                .id(cart.getId())
                .customerId(cart.getCustomer().getId())
                .laptopOnCartsDTOs(
                        Optional.ofNullable(cart.getLaptopOnCarts())
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(LaptopOnCartMapper::convertToDTO)
                                .collect(Collectors.toList())
                )
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
