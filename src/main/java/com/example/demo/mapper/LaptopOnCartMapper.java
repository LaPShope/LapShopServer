package com.example.demo.mapper;

import com.example.demo.DTO.LaptopOnCartDTO;
import com.example.demo.DTO.Response.CartResponse.CartItem;
import com.example.demo.DTO.Response.LaptopOnCartResponse;
import com.example.demo.Models.LaptopOnCart;

public class LaptopOnCartMapper {
    public static LaptopOnCartDTO convertToDTO(LaptopOnCart laptopOnCart) {
        return LaptopOnCartDTO.builder()
                .id(laptopOnCart.getId())
                .cartId(laptopOnCart.getCart().getId())
                .laptopModelId(laptopOnCart.getLaptopModel().getId())
                .quantity(laptopOnCart.getQuantity())
                .build();
    }

    public static LaptopOnCartResponse convertToResponse(LaptopOnCart laptopOnCart) {
        return LaptopOnCartResponse.builder()
                .id(laptopOnCart.getId())
                .cart(CartMapper.convertToDTO(laptopOnCart.getCart()))
                .laptopModel(LaptopModelMapper.convertToDTO(laptopOnCart.getLaptopModel()))
                .quantity(laptopOnCart.getQuantity())
                .build();
    }

    public static CartItem convertToItem(LaptopOnCart laptopOnCart) {
        return CartItem.builder()
                .id(laptopOnCart.getId())
                .cartId(laptopOnCart.getCart().getId())
                .laptopModel(LaptopModelMapper.convertToDTO(laptopOnCart.getLaptopModel()))
                .quantity(laptopOnCart.getQuantity())
                .build();
    }
}
