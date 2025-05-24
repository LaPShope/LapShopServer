package com.example.demo.mapper;

import com.example.demo.dto.LaptopOnCartDTO;
import com.example.demo.dto.response.cart.CartItem;
import com.example.demo.dto.response.LaptopOnCartResponse;
import com.example.demo.model.LaptopOnCart;

public class LaptopOnCartMapper {
    public static LaptopOnCartDTO convertToDTO(LaptopOnCart laptopOnCart) {
        return LaptopOnCartDTO.builder()
                .cartId(laptopOnCart.getCart().getId())
                .laptopModelId(laptopOnCart.getLaptopModel().getId())
                .quantity(laptopOnCart.getQuantity())
                .build();
    }

    public static LaptopOnCartResponse convertToResponse(LaptopOnCart laptopOnCart) {
        return LaptopOnCartResponse.builder()
                .cart(CartMapper.convertToDTO(laptopOnCart.getCart()))
                .laptopModel(LaptopModelMapper.convertToDTO(laptopOnCart.getLaptopModel()))
                .quantity(laptopOnCart.getQuantity())
                .build();
    }

    public static CartItem convertToItem(LaptopOnCart laptopOnCart) {
        return CartItem.builder()
                .cartId(laptopOnCart.getCart().getId())
                .laptopModel(LaptopModelMapper.convertToDTO(laptopOnCart.getLaptopModel()))
                .quantity(laptopOnCart.getQuantity())
                .build();
    }
}
