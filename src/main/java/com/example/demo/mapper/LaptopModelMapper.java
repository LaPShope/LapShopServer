package com.example.demo.mapper;

import com.example.demo.DTO.LaptopModelDTO;
import com.example.demo.Models.*;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class LaptopModelMapper {
    public static LaptopModelDTO convertToDTO(LaptopModel laptopModel) {
        LaptopModelDTO laptopModelDTO = LaptopModelDTO.builder()
                .id(laptopModel.getId())
                .name(laptopModel.getName())
                .branch(laptopModel.getBranch())
                .cpu(laptopModel.getCpu())
                .ram(laptopModel.getRam())
                .storage(laptopModel.getStorage())
                .display(laptopModel.getDisplay())
                .color(laptopModel.getColor())
                .price(laptopModel.getPrice())
                .description(laptopModel.getDescription())
                .build();

        // Ánh xạ thủ công các danh sách liên quan (list ID), đảm bảo không bị null
        laptopModelDTO.setLaptopIds(
                Optional.ofNullable(laptopModel.getLaptopList())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(Laptop::getMacId)
                        .collect(Collectors.toList())
        );

        laptopModelDTO.setImageIds(
                Optional.ofNullable(laptopModel.getImageList())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(Image::getId)
                        .collect(Collectors.toList())
        );

        laptopModelDTO.setCommentIds(
                Optional.ofNullable(laptopModel.getCommentList())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(Comment::getId)
                        .collect(Collectors.toList())
        );

        laptopModelDTO.setLaptopOnCartIds(
                Optional.ofNullable(laptopModel.getLaptopOnCartList())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(LaptopOnCart::getId)
                        .collect(Collectors.toList())
        );

        laptopModelDTO.setOrderDetailIds(
                Optional.ofNullable(laptopModel.getOrderDetailList())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(OrderDetail::getId)
                        .collect(Collectors.toList())
        );

        laptopModelDTO.setSaleIds(
                Optional.ofNullable(laptopModel.getSaleList())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(Sale::getId)
                        .collect(Collectors.toList())
        );

        return laptopModelDTO;
    }
}
