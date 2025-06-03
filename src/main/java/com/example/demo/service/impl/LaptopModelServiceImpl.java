package com.example.demo.service.impl;

import com.example.demo.common.AuthUtil;
import com.example.demo.dto.LaptopModelDTO;
import com.example.demo.dto.response.LaptopModelResponse;
import com.example.demo.dto.response.PagingResponse;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.LaptopModelService;
import com.example.demo.mapper.LaptopModelMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class LaptopModelServiceImpl implements LaptopModelService {
    private final LaptopModelRepository laptopModelRepository;
    private final ImageRepository imageRepository;
    private final RedisService redisService;

    public LaptopModelServiceImpl(RedisService redisService, LaptopModelRepository laptopModelRepository, ImageRepository imageRepository) {
        this.laptopModelRepository = laptopModelRepository;
        this.redisService = redisService;
        this.imageRepository = imageRepository;
    }

    // 1. Lấy tất cả LaptopModel
    @Transactional
    @Override
    public List<LaptopModelResponse> getAllLaptopModels() {
        List<LaptopModelResponse> cachedLaptopResponses = redisService.getObject("allLaptopModel", new TypeReference<List<LaptopModelResponse>>() {
        });
        if (cachedLaptopResponses != null && !cachedLaptopResponses.isEmpty()) {
            return cachedLaptopResponses;
        }

        List<LaptopModelResponse> laptopModelResponses = laptopModelRepository.findAll().stream()
                .map(LaptopModelMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allLaptopModel", laptopModelResponses, 600);
        return laptopModelResponses;
    }

    // 2. Lấy LaptopModel theo ID
    @Transactional
    @Override
    public LaptopModelResponse getLaptopModelById(UUID id) {
        LaptopModelResponse cachedLaptopResponses = redisService.getObject("allLaptopModel", new TypeReference<LaptopModelResponse>() {
        });
        if (cachedLaptopResponses != null) {
            return cachedLaptopResponses;
        }

        // Tìm LaptopModel theo ID, nếu không tìm thấy thì ném ngoại lệ
        LaptopModel laptopModel = laptopModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laptop Model with ID " + id + " not found"));

        LaptopModelResponse laptopResponse = LaptopModelMapper.convertToResponse(laptopModel);

        redisService.setObject("laptopModel", laptopResponse, 600);

        return laptopResponse;
    }

    @Transactional
    @Override
    public LaptopModelResponse createLaptopModel(LaptopModelDTO laptopModelDTO) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }

        LaptopModel laptopModel = LaptopModel.builder()
                .id(null)
                .name(laptopModelDTO.getName())
                .brand(laptopModelDTO.getBrand())
                .cpu(laptopModelDTO.getCpu())
                .gpu(laptopModelDTO.getGpu())
                .ram(laptopModelDTO.getRam())
                .storage(laptopModelDTO.getStorage())
                .display(laptopModelDTO.getDisplay())
                .color(laptopModelDTO.getColor())
                .price(laptopModelDTO.getPrice())
                .description(laptopModelDTO.getDescription())
                .build();

        LaptopModel laptopModelExisting = laptopModelRepository.save(laptopModel);

        LaptopModelResponse laptopModelResponse = LaptopModelMapper.convertToResponse(laptopModelExisting);

        redisService.deleteByPatterns(List.of("allLaptopModel", "allImage", "allSale", "allLaptopOnSale"));
        redisService.setObject("laptopModel:" + laptopModelResponse.getId(), laptopModelResponse, 600);

        return laptopModelResponse;
    }

    // 4. Cập nhật LaptopModel
    @Transactional
    @Override
    public LaptopModelResponse updateLaptopModel(UUID id, LaptopModelDTO laptopModelDTO) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }

        LaptopModel existingLaptopModel = laptopModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laptop Model with ID " + id + " not found"));

        BeanUtils.copyProperties(laptopModelDTO, existingLaptopModel, "id");

        LaptopModel laptopModel = laptopModelRepository.save(existingLaptopModel);
        LaptopModelResponse laptopModelResponse = LaptopModelMapper.convertToResponse(laptopModel);

        redisService.deleteByPatterns(List.of("allLaptopModel", "allImage", "allSale", "laptopModel:" + id, "*derDetail*", "allLaptopOnSale"));
        redisService.setObject("laptopModel:" + id, laptopModelResponse, 600);

        return laptopModelResponse;
    }

    @Override
    public LaptopModelResponse partialUpdateLaptopModel(UUID id, Map<String, Object> fieldsToUpdate) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }

        LaptopModel laptopModel = laptopModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LaptopModel with ID " + id + " not found!"));

        Class<?> clazz = laptopModel.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    if (field.getType().equals(BigDecimal.class)) {
                        // Chuyển đổi BigDecimal
                        field.set(laptopModel, new BigDecimal(newValue.toString()));
                    } else if (field.getType().isEnum()) {
                        // Xử lý Enum (Color)
                        try {
                            Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), newValue.toString());
                            field.set(laptopModel, enumValue);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid enum value for field: " + fieldName);
                        }
                    } else {
                        // Cập nhật các kiểu dữ liệu khác
                        field.set(laptopModel, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        LaptopModel updatedLaptopModel = laptopModelRepository.save(laptopModel);
        LaptopModelResponse laptopModelResponse = LaptopModelMapper.convertToResponse(updatedLaptopModel);

        redisService.deleteByPatterns(List.of("allLaptopModel", "allImage", "allSale", "laptopModel:" + id, "*derDetail*", "allLaptopOnSale"));
        redisService.setObject("laptopModel:" + id, laptopModelResponse, 600);

        return laptopModelResponse;
    }

    // 5. Xóa LaptopModel

    @Override
    @Transactional
    public void deleteLaptopModel(UUID id) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }
        boolean isExist = laptopModelRepository.existsById(id);
        if (!isExist) {
            throw new EntityNotFoundException("Laptop Model with ID " + id + " not found");
        }

        System.out.println("Deleting LaptopModel with ID: " + id);
//        redisService.deleteByPatterns(List.of("allLaptopModel", "allImage", "allSale", "laptopModel:" + id, "orderDetail", "*derDetail*", "allLaptopOnSale"));
        laptopModelRepository.deleteById(id);
    }

    @Override
    public PagingResponse<?> getLaptopModelsWithPagination(int offset, int pageSize) {

        Page<LaptopModel> result = laptopModelRepository.findAll(PageRequest.of(offset, pageSize));

        List<LaptopModelResponse> laptopList = result.getContent().stream()
                .map(LaptopModelMapper::convertToResponse)
                .collect(Collectors.toList());

        PagingResponse<?> laptopResponses = PagingResponse.builder()
                .recordCount(result.getNumberOfElements())
                .response(laptopList)
                .build();

        return laptopResponses;
    }


    @Override
    public PagingResponse<?> getLaptopModelsWithPaginationAndSortByPriceASC(double price, int offset, int pageSize) {
        Page<LaptopModel> result = laptopModelRepository.findAll(PageRequest.of(offset, pageSize, Sort.by("price").ascending()));

        List<LaptopModelResponse> laptopList = result.getContent().stream()
                .map(LaptopModelMapper::convertToResponse)
                .collect(Collectors.toList());

        PagingResponse<?> laptopResponses = PagingResponse.builder()
                .recordCount(result.getNumberOfElements())
                .response(laptopList)
                .build();

        return laptopResponses;
    }

    @Override
    public PagingResponse<?> getLaptopModelsWithPaginationAndSortByPriceDES(double price, int offset, int pageSize) {

        Page<LaptopModel> result = laptopModelRepository.findAll(PageRequest.of(offset, pageSize, Sort.by("price").descending()));

        List<LaptopModelResponse> laptopList = result.getContent().stream()
                .map(LaptopModelMapper::convertToResponse)
                .collect(Collectors.toList());

        PagingResponse<?> laptopResponses = PagingResponse.builder()
                .recordCount(result.getNumberOfElements())
                .response(laptopList)
                .build();

        return laptopResponses;
    }

    @Override
    @Transactional
    public LaptopModelResponse addImageToLaptopModel(UUID laptopModelId, UUID imageId) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }

        LaptopModel laptopModel = laptopModelRepository
                .findById(laptopModelId)
                .orElseThrow(() -> new EntityNotFoundException("LaptopModel with ID " + laptopModelId + " not found"));
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image with ID " + imageId + " not found"));

        // check image exist in laptopModel
        if (laptopModel.getImageList().contains(image)) {
            throw new IllegalArgumentException("Image with ID " + imageId + " already exists in LaptopModel with ID " + laptopModelId);
        }

        laptopModel.getImageList().add(image);
        image.getLaptopModelList().add(laptopModel);
        laptopModelRepository.save(laptopModel);

        LaptopModelResponse laptopModelResponse = LaptopModelMapper.convertToResponse(laptopModel);
        redisService.deleteByPatterns(List.of("allLaptopModel", "allImage", "allSale", "laptopModel:" + laptopModelId, "*derDetail*", "allLaptopOnSale"));
        redisService.setObject("laptopModel:" + laptopModelId, laptopModelResponse, 600);
        redisService.setObject("image:" + imageId, image, 600);
        return laptopModelResponse;
    }

    @Override
    @Transactional
    public LaptopModelResponse addImageToLaptopModelV2(UUID laptopModelId, List<UUID> imageIds) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }

        LaptopModel laptopModel = laptopModelRepository
                .findById(laptopModelId)
                .orElseThrow(() -> new EntityNotFoundException("LaptopModel with ID " + laptopModelId + " not found"));

        // Remove this laptopModel from all old images' laptopModelList
        for (Image oldImage : new ArrayList<>(laptopModel.getImageList())) {
            oldImage.getLaptopModelList().remove(laptopModel);
        }

        // Fetch new images and set them
        List<Image> newImages = imageRepository.findAllById(imageIds);
        laptopModel.setImageList(new ArrayList<>(newImages));

        // Add this laptopModel to each new image's laptopModelList
        for (Image image : newImages) {
            if (!image.getLaptopModelList().contains(laptopModel)) {
                image.getLaptopModelList().add(laptopModel);
            }
        }

        laptopModelRepository.save(laptopModel);

        LaptopModelResponse laptopModelResponse = LaptopModelMapper.convertToResponse(laptopModel);
        redisService.deleteByPatterns(List.of("allLaptopModel", "allImage", "allSale", "laptopModel:" + laptopModelId, "*derDetail*", "allLaptopOnSale"));
        redisService.setObject("laptopModel:" + laptopModelId, laptopModelResponse, 600);
        for (UUID imageId : imageIds) {
            redisService.setObject("image:" + imageId, newImages.stream().filter(img -> img.getId().equals(imageId)).findFirst().orElse(null), 600);
        }

        return laptopModelResponse;
    }
}