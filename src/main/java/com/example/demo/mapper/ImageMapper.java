package com.example.demo.mapper;

import com.example.demo.dto.ImageDTO;
import com.example.demo.model.Image;

public class ImageMapper {
    public static ImageDTO convertToDTO(Image image) {
        return ImageDTO.builder()
                .id(image.getId())
//                .laptopModelIds(image.getLaptopModelList() == null ? null
//                        : image.getLaptopModelList().stream()
//                        .map(LaptopModel::getId)
//                        .collect(Collectors.toList()))
                .imageUrl(image.getImageUrl())
                .build();
    }
}
