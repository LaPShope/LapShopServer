package com.example.demo.mapper;

import com.example.demo.dto.LaptopModelDTO;
import com.example.demo.dto.response.LaptopModelResponse;
import com.example.demo.model.*;

import java.util.Collections;
import java.util.stream.Collectors;

public class LaptopModelMapper {
    public static LaptopModelDTO convertToDTO(LaptopModel laptopModel) {
        return LaptopModelDTO.builder()
                .id(laptopModel.getId())
                .name(laptopModel.getName())
                .brand(laptopModel.getBrand())
                .cpu(laptopModel.getCpu())
                .gpu(laptopModel.getGpu())
                .ram(laptopModel.getRam())
                .storage(laptopModel.getStorage())
                .display(laptopModel.getDisplay())
                .color(laptopModel.getColor())
                .price(laptopModel.getPrice())
                .description(laptopModel.getDescription())
                .images(ImageMapper.convertToDTO(laptopModel.getImageList()))
                .build();
    }

    public static LaptopModelResponse convertToResponse(LaptopModel laptopModel) {
        return LaptopModelResponse.builder()
                .id(laptopModel.getId())
                .name(laptopModel.getName())
                .brand(laptopModel.getBrand())
                .cpu(laptopModel.getCpu())
                .ram(laptopModel.getRam())
                .gpu(laptopModel.getGpu())
                .storage(laptopModel.getStorage())
                .display(laptopModel.getDisplay())
                .color(laptopModel.getColor())
                .price(laptopModel.getPrice())
                .description(laptopModel.getDescription())
                .commentList(laptopModel.getCommentList() == null ? Collections.emptyList() : laptopModel.getCommentList().stream().filter(comment -> comment.getParent() == null).map(CommentMapper::convertToItems).collect(Collectors.toList()))
                .saleList(laptopModel.getSaleList() == null ? Collections.emptyList() : laptopModel.getSaleList().stream().map(SaleMapper::convertToDTO).collect(Collectors.toList()))
                .imageList(laptopModel.getImageList() == null ? Collections.emptyList() : laptopModel.getImageList().stream().map(ImageMapper::convertToDTO).collect(Collectors.toList()))
                .build();
    }
}
