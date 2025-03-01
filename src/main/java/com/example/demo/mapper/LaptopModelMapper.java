package com.example.demo.mapper;

import com.example.demo.DTO.LaptopModelDTO;
import com.example.demo.DTO.Response.LaptopModelResponse;
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
        return laptopModelDTO;
    }

    public static LaptopModelResponse convertToResponse(LaptopModel laptopModel) {
        LaptopModelResponse laptopModelResponse = LaptopModelResponse.builder()
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
                .commentList(laptopModel.getCommentList() == null ? Collections.emptyList() : laptopModel.getCommentList().stream().filter(comment -> comment.getParent() == null).map(CommentMapper::convertToItems).collect(Collectors.toList()))
                .saleList(laptopModel.getSaleList() == null ? Collections.emptyList() : laptopModel.getSaleList().stream().map(SaleMapper::convertToDTO).collect(Collectors.toList()))
                .imageList(laptopModel.getImageList() == null ? Collections.emptyList() : laptopModel.getImageList().stream().map(ImageMapper::convertToDTO).collect(Collectors.toList()))
                .build();
        return laptopModelResponse;
    }
}
