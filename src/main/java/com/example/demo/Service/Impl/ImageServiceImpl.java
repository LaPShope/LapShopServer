package com.example.demo.Service.Impl;

import com.example.demo.DTO.ImageDTO;
import com.example.demo.Models.Image;
import com.example.demo.Models.LaptopModel;
import com.example.demo.Repository.ImageRepository;
import com.example.demo.Repository.LaptopModelRepository;
import com.example.demo.Service.ImageService;

import com.example.demo.mapper.ImageMapper;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
@Transactional
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final LaptopModelRepository laptopModelRepository;

    public ImageServiceImpl(ImageRepository imageRepository, LaptopModelRepository laptopModelRepository) {
        this.imageRepository = imageRepository;
        this.laptopModelRepository = laptopModelRepository;
    }

    // 1. Lấy danh sách tất cả Images
    @Override
    public List<ImageDTO> getAllImages() {
        return imageRepository.findAll().stream()
                .map(ImageMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Lấy Image theo ID
    @Override
    public ImageDTO getImageById(UUID id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + id));
        return ImageMapper.convertToDTO(image);
    }

    // 3. Tạo mới một Image
    @Override
    public ImageDTO createImage(ImageDTO imageDTO) {
        Image image = Image.builder()
                .id(null)
                .imageUrl(imageDTO.getImageUrl())
                .build();

        if (imageDTO.getLaptopModelIds() != null && !imageDTO.getLaptopModelIds().isEmpty()) {
            List<LaptopModel> laptopModels = imageDTO.getLaptopModelIds().stream()
                    .map(laptopModelId -> laptopModelRepository.findById(laptopModelId)
                            .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found")))
                    .collect(Collectors.toList());
            image.setLaptopModelList(laptopModels);
        }else{
            throw  new IllegalArgumentException("LaptopModel cannot be null");
        }
        Image imageExisting = imageRepository.save(image);

        return ImageMapper.convertToDTO(imageExisting);
    }

    @Override
    public ImageDTO updateImage(UUID imageId, ImageDTO imageDTO){
        Image imageExisting = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));
        imageExisting.setImageUrl(imageDTO.getImageUrl());

        if(imageDTO.getLaptopModelIds() == null){
            throw new IllegalArgumentException("LaptopModel cannot be null");
        }
        else{
            List<LaptopModel> laptopModels = imageDTO.getLaptopModelIds().stream()
                    .map(laptopModelId -> laptopModelRepository.findById(laptopModelId)
                            .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found")))
                    .collect(Collectors.toList());
            imageExisting.setLaptopModelList(laptopModels);
        }

        Image image = imageRepository.save(imageExisting);
        return ImageMapper.convertToDTO(image);
    }

    @Override
    public ImageDTO partialUpdateImage(UUID id, Map<String, Object> fieldsToUpdate) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image with ID " + id + " not found!"));

        Class<?> clazz = image.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    field.set(image, newValue);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Image updatedImage = imageRepository.save(image);
        return ImageMapper.convertToDTO(updatedImage);
    }

    // 5. Xóa Image theo ID
    @Override
    public void deleteImage(UUID id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        image.setLaptopModelList(null);

        imageRepository.delete(image);
    }


}