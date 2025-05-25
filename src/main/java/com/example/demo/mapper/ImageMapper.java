package com.example.demo.mapper;

import com.example.demo.dto.ImageDTO;
import com.example.demo.model.Image;

import java.util.List;

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

    public static List<ImageDTO> convertToDTO(List<Image> images) {
        return images.stream()
                .map(ImageMapper::convertToDTO)
                .toList();
    }
}
