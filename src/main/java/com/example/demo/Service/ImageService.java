package com.example.demo.Service;

import com.example.demo.DTO.ImageDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ImageService {
    ImageDTO partialUpdateImage(UUID id, Map<String,Object> fieldsToUpdate);
    List<ImageDTO> getAllImages();
    ImageDTO getImageById(UUID id);
    ImageDTO createImage(ImageDTO imageDTO);
    ImageDTO updateImage(UUID imageId, ImageDTO imageDTO);
    void deleteImage(UUID id);
}
