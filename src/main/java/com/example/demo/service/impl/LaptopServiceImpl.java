package com.example.demo.service.impl;

import com.example.demo.common.ConvertDate;
import com.example.demo.dto.LaptopDTO;
import com.example.demo.dto.response.LaptopResponse;
import com.example.demo.model.Laptop;
import com.example.demo.model.LaptopModel;
import com.example.demo.repository.LaptopQueryRepository;
import com.example.demo.repository.LaptopRepository;
import com.example.demo.repository.LaptopModelRepository;
import com.example.demo.service.LaptopService;

import com.example.demo.mapper.LaptopMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.*;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class LaptopServiceImpl implements LaptopService {
    private final RedisService redisService;
    private final LaptopQueryRepository laptopQueryRepository;
    private final LaptopRepository laptopRepository;
    private final LaptopModelRepository laptopModelRepository;

    public LaptopServiceImpl( RedisService redisService, LaptopQueryRepository laptopQueryRepository, LaptopRepository laptopRepository, LaptopModelRepository laptopModelRepository) {
        this.laptopRepository = laptopRepository;
        this.laptopModelRepository = laptopModelRepository;
        this.laptopQueryRepository = laptopQueryRepository;
        this.redisService = redisService;
    }

    @Override
    public List<LaptopResponse> getAllLaptops() {
        List<LaptopResponse> cachedLaptopResponses = redisService.getObject("allLaptop", new TypeReference<List<LaptopResponse>>() {});
        if(cachedLaptopResponses != null && !cachedLaptopResponses.isEmpty()){
            return cachedLaptopResponses;
        }

        List<LaptopResponse> laptopResponses = laptopRepository.findAll().stream()
                .map(LaptopMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allLaptop",laptopResponses,600);

        return laptopResponses;
    }

    @Override
    public LaptopResponse getLaptopById(UUID id) {
        LaptopResponse cachedLaptopResponses = redisService.getObject("allLaptop", new TypeReference<LaptopResponse>() {});
        if(cachedLaptopResponses != null){
            return cachedLaptopResponses;
        }

        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laptop not found!"));
        LaptopResponse laptopResponse = LaptopMapper.convertToResponse(laptop);

        redisService.setObject("laptop:"+id,laptopResponse,600);

        return laptopResponse;
    }

    // **3. Tạo mới Laptop**
    @Transactional
    @Override
    public LaptopResponse createLaptop(LaptopDTO laptopDTO) {
        Laptop laptop = Laptop.builder()
                .MFG(laptopDTO.getMFG())
                .status(laptopDTO.getStatus())
                .build();

        if(laptopDTO.getLaptopModelId() == null){
            throw  new IllegalArgumentException("LaptopModel cannot be null");
        }

        LaptopModel laptopModel = laptopModelRepository.findById(laptopDTO.getLaptopModelId())
                .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found"));

        laptop.setLaptopModel(laptopModel);
//        laptopModel.addLaptop(laptop);

        Laptop laptopExisting = laptopRepository.save(laptop);

        LaptopResponse laptopResponse = LaptopMapper.convertToResponse(laptopExisting);

        redisService.deleteByPatterns(List.of("allLaptop","allLaptopModel","*searchLaptop*","allLaptopOnSale"));
        redisService.setObject("laptop:"+laptopResponse.getMacId(),laptopResponse,600);

        return laptopResponse;
    }

    // **4. Cập nhật Laptop**
    @Override
    @Transactional
    public LaptopResponse updateLaptop(UUID laptopId, LaptopDTO updatedLaptopDTO) {

        Laptop existingLaptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new EntityNotFoundException("Laptop not found"));

        LaptopModel newModel = laptopModelRepository.findById(updatedLaptopDTO.getLaptopModelId())
                .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found"));

        if(updatedLaptopDTO.getLaptopModelId() == null){
            throw  new IllegalArgumentException("LaptopModel cannot be null");
        }
        else if (existingLaptop.getLaptopModel() == null ||!existingLaptop.getLaptopModel().getId().equals(updatedLaptopDTO.getLaptopModelId())) {
            existingLaptop.setLaptopModel(newModel);
        }

        existingLaptop.setMFG(updatedLaptopDTO.getMFG());
        existingLaptop.setStatus(updatedLaptopDTO.getStatus());

        Laptop laptopExisting = laptopRepository.save(existingLaptop);
        LaptopResponse laptopResponse = LaptopMapper.convertToResponse(laptopExisting);

        redisService.deleteByPatterns(List.of("allLaptop","allLaptopModel","laptop:"+laptopId,"*searchLaptop*","allLaptopOnSale"));
        redisService.setObject("laptop:"+laptopId,laptopResponse,600);

        return laptopResponse;
    }

    @Transactional
    @Override
    public LaptopResponse partialUpdateLaptop(UUID id, Map<String, Object> fieldsToUpdate) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laptop with ID " + id + " not found!"));

        Class<?> clazz = laptop.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    if (field.getType().isEnum()) {
                        try {
                            Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), newValue.toString());
                            field.set(laptop, enumValue);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid enum value for field: " + fieldName);
                        }
                    }
                    else if (field.getType().equals(Date.class)){
                        Date parsedDate = ConvertDate.convertToDate(newValue);
                        field.set(laptop, parsedDate);
                    }else {
                        field.set(laptop, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Laptop updatedLaptop = laptopRepository.save(laptop);
        LaptopResponse laptopResponse = LaptopMapper.convertToResponse(updatedLaptop);

        redisService.deleteByPatterns(List.of("allLaptop","allLaptopModel","laptop:"+id,"*searchLaptop*","allLaptopOnSale"));
        redisService.setObject("laptop:"+id,laptopResponse,600);

        return laptopResponse;
    }


    // **5. Xóa Laptop theo ID**
    @Transactional
    @Override
    public void deleteLaptop(UUID id) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laptop not found"));

//        LaptopModel laptopModel = laptop.getLaptopModel();
//        laptopModel.removeLaptop(laptop);
        redisService.deleteByPatterns(List.of("allLaptop","allLaptopModel","laptop:"+id,"*searchLaptop*","allLaptopOnSale"));

        laptopRepository.deleteById(id);
    }

    @Override
    public List<LaptopResponse> searchLaptops(Map<String, Object> filters) {
        String cacheKey = "searchLaptop";

        if (!filters.isEmpty()) {
            cacheKey += filters.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining("|"));
        }

        List<LaptopResponse> cachedLaptopResponses = redisService.getObject(cacheKey, new TypeReference<List<LaptopResponse>>() {});

        if(cachedLaptopResponses != null && !cachedLaptopResponses.isEmpty()){
            return cachedLaptopResponses;
        }

        List<LaptopResponse> laptopResponses = laptopQueryRepository.searchLaptopsByLaptopModelAndLaptop(filters);

        redisService.setObject(cacheKey,laptopResponses,600);

        return laptopResponses;
    }


    public List<LaptopResponse> searchLaptopsOnSale(){
        List<LaptopResponse> cachedLaptopResponses = redisService.getObject("allLaptopOnSale", new TypeReference<List<LaptopResponse>>() {});;;
        if(cachedLaptopResponses != null && !cachedLaptopResponses.isEmpty()){
            return cachedLaptopResponses;
        }

        List<LaptopResponse> laptopsOnSale = laptopRepository.findLaptopsOnSale(new Date())
                .stream()
                .map(LaptopMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allLaptopOnSale",cachedLaptopResponses,600);

        return laptopsOnSale;
    }



}