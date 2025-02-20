package com.example.demo.mapper;

import com.example.demo.DTO.LaptopOnCartDTO;
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
}
