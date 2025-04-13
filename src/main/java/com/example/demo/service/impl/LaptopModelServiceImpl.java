package com.example.demo.service.impl;

import com.example.demo.common.AuthUtil;
import com.example.demo.dto.LaptopModelDTO;
import com.example.demo.dto.response.LaptopModelResponse;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.LaptopModelService;
import com.example.demo.mapper.LaptopModelMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class LaptopModelServiceImpl implements LaptopModelService {

    private final LaptopModelRepository laptopModelRepository;
    private final RedisService redisService;

    public LaptopModelServiceImpl(RedisService redisService, LaptopModelRepository laptopModelRepository) {
        this.laptopModelRepository = laptopModelRepository;
        this.redisService = redisService;
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
                .branch(laptopModelDTO.getBranch())
                .cpu(laptopModelDTO.getCpu())
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
    public void deleteLaptopModel(UUID id) {
        if (!AuthUtil.isAdmin()) {
            throw new SecurityException("User is not an Admin");
        }

        LaptopModel laptopModel = laptopModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laptop Model not found"));

        redisService.deleteByPatterns(List.of("allLaptopModel", "allImage", "allSale", "laptopModel:" + id, "orderDetail", "*derDetail*", "allLaptopOnSale"));

        laptopModelRepository.delete(laptopModel);
    }


}