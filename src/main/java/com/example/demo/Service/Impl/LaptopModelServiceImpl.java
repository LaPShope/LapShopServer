package com.example.demo.Service.Impl;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.ImageDTO;
import com.example.demo.DTO.LaptopModelDTO;
import com.example.demo.DTO.Response.LaptopModelResponse;
import com.example.demo.DTO.Response.LaptopResponse;
import com.example.demo.Models.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.LaptopModelService;
import com.example.demo.mapper.LaptopModelMapper;
import com.example.demo.mapper.LaptopOnCartMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Column;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.modelmapper.ModelMapper;
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
        List<LaptopModelResponse> cachedLaptopResponses = redisService.getObject("allLaptopModel", new TypeReference<List<LaptopModelResponse>>() {});
        if(cachedLaptopResponses != null && !cachedLaptopResponses.isEmpty()){
            return cachedLaptopResponses;
        }

        List<LaptopModelResponse> laptopModelResponses = laptopModelRepository.findAll().stream()
                .map(LaptopModelMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allLaptopModel",laptopModelResponses,600);

        return laptopModelResponses;
    }

    // 2. Lấy LaptopModel theo ID
    @Transactional
    @Override
    public LaptopModelResponse getLaptopModelById(UUID id) {
        LaptopModelResponse cachedLaptopResponses = redisService.getObject("allLaptopModel", new TypeReference<LaptopModelResponse>() {});
        if(cachedLaptopResponses != null){
            return cachedLaptopResponses;
        }

        // Tìm LaptopModel theo ID, nếu không tìm thấy thì ném ngoại lệ
        LaptopModel laptopModel = laptopModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laptop Model with ID " + id + " not found"));

        LaptopModelResponse laptopResponse = LaptopModelMapper.convertToResponse(laptopModel);

        redisService.setObject("laptopModel",laptopResponse,600);

        return laptopResponse;
    }
    @Transactional
    @Override
    public LaptopModelResponse createLaptopModel(LaptopModelDTO laptopModelDTO) {
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

        redisService.deleteByPatterns(List.of("allLaptopModel","allImage","allSale"));
        redisService.setObject("laptopModel:"+laptopModelResponse.getId(),laptopModelResponse,600);

        return laptopModelResponse;
    }

    // 4. Cập nhật LaptopModel
    @Transactional
    @Override
    public LaptopModelResponse updateLaptopModel(UUID id, LaptopModelDTO laptopModelDTO) {
        LaptopModel existingLaptopModel = laptopModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laptop Model with ID " + id + " not found"));

        BeanUtils.copyProperties(laptopModelDTO, existingLaptopModel, "id");

        LaptopModel laptopModel = laptopModelRepository.save(existingLaptopModel);
        LaptopModelResponse laptopModelResponse = LaptopModelMapper.convertToResponse(laptopModel);

        redisService.deleteByPatterns(List.of("allLaptopModel","allImage","allSale","laptopModel:"+id,"*derDetail*"));
        redisService.setObject("laptopModel:"+id,laptopModelResponse,600);

        return laptopModelResponse;
    }

    @Override
    public LaptopModelResponse partialUpdateLaptopModel(UUID id, Map<String, Object> fieldsToUpdate) {
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

        redisService.deleteByPatterns(List.of("allLaptopModel","allImage","allSale","laptopModel:"+id,"*derDetail*"));
        redisService.setObject("laptopModel:"+id,laptopModelResponse,600);

        return laptopModelResponse;
    }

    // 5. Xóa LaptopModel

    @Override
    public void deleteLaptopModel(UUID id) {
        LaptopModel laptopModel = laptopModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laptop Model not found"));

        redisService.deleteByPatterns(List.of("allLaptopModel","allImage","allSale","laptopModel:"+id,"orderDetail","*derDetail*"));

        laptopModelRepository.delete(laptopModel);
    }


}