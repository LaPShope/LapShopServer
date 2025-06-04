package com.example.demo.service.impl;

import com.example.demo.common.AuthUtil;
import com.example.demo.dto.ImageDTO;
import com.example.demo.model.Image;
import com.example.demo.model.LaptopModel;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.LaptopModelRepository;
import com.example.demo.service.ImageService;

import com.example.demo.mapper.ImageMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
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
    private final RedisService redisService;
    private final ImageRepository imageRepository;
    private final LaptopModelRepository laptopModelRepository;

    public ImageServiceImpl(RedisService redisService, ImageRepository imageRepository, LaptopModelRepository laptopModelRepository) {
        this.imageRepository = imageRepository;
        this.laptopModelRepository = laptopModelRepository;
        this.redisService = redisService;
    }

    // 1. Lấy danh sách tất cả Images
    @Override
    public List<ImageDTO> getAllImages() {
        List<ImageDTO> cachedImage = redisService.getObject("allImage", new TypeReference<List<ImageDTO>>() {
        });

        if (cachedImage != null && !cachedImage.isEmpty()) {
            return cachedImage;
        }

        List<ImageDTO> imageDTOS = imageRepository.findAll().stream()
                .map(ImageMapper::convertToDTO)
                .collect(Collectors.toList());

        redisService.setObject("allImage", imageDTOS, 600);

        return imageDTOS;
    }

    // 2. Lấy Image theo ID
    @Override
    public ImageDTO getImageById(UUID id) {
        ImageDTO cachedImage = redisService.getObject("allImage", new TypeReference<ImageDTO>() {
        });
        if (cachedImage != null) {
            return cachedImage;
        }

        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + id));
        ImageDTO imageDTO = ImageMapper.convertToDTO(image);

        redisService.setObject("image:" + id, imageDTO, 600);

        return imageDTO;
    }

    @Transactional
    @Override
    public ImageDTO createImage(ImageDTO imageDTO) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }

        Image image = Image.builder()
                .id(null)
                .imageUrl(imageDTO.getImageUrl())
                .build();

        if (imageDTO.getLaptopModelIds() != null && !imageDTO.getLaptopModelIds().isEmpty()) {
            List<LaptopModel> laptopModels = imageDTO.getLaptopModelIds()
                    .stream()
                    .map(laptopModelId -> laptopModelRepository.findById(laptopModelId)
                            .orElseThrow(() -> new EntityNotFoundException("LaptopModel with ID " + laptopModelId + " not found")))
                    .collect(Collectors.toList());
            image.setLaptopModelList(laptopModels);
        }

        Image imageExisting = imageRepository.save(image);
        ImageDTO cachedImage = ImageMapper.convertToDTO(imageExisting);

        redisService.deleteByPatterns(List.of("allImage", "*laptopModel*"));
        redisService.setObject("image:" + imageDTO.getId(), cachedImage, 600);

        return cachedImage;
    }

    @Transactional
    @Override
    public ImageDTO updateImage(UUID imageId, ImageDTO imageDTO) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }

        Image imageExisting = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));
        imageExisting.setImageUrl(imageDTO.getImageUrl());

        if (imageDTO.getLaptopModelIds() == null) {
            throw new IllegalArgumentException("LaptopModel cannot be null");
        } else {
            List<LaptopModel> laptopModels = imageDTO.getLaptopModelIds().stream()
                    .map(laptopModelId -> laptopModelRepository.findById(laptopModelId)
                            .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found")))
                    .collect(Collectors.toList());
            imageExisting.setLaptopModelList(laptopModels);
        }

        Image image = imageRepository.save(imageExisting);
        ImageDTO cachedImage = ImageMapper.convertToDTO(image);

        redisService.deleteByPatterns(List.of("allImage", "*aptopModel*"));
        redisService.setObject("image:" + imageDTO.getId(), cachedImage, 600);

        return cachedImage;
    }

    @Transactional
    @Override
    public ImageDTO partialUpdateImage(UUID id, Map<String, Object> fieldsToUpdate) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }


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
        ImageDTO cachedImage = ImageMapper.convertToDTO(updatedImage);

        redisService.deleteByPatterns(List.of("allImage", "*aptopModel*"));
        redisService.setObject("image:" + id, cachedImage, 600);

        return cachedImage;
    }

    @Transactional
    @Override
    public void deleteImage(UUID id) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }

        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        image.setLaptopModelList(null);

        redisService.deleteByPatterns(List.of("allImage", "*aptopModel*"));

        imageRepository.delete(image);
    }


}