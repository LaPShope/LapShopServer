package com.example.demo.Service.Impl;

import com.example.demo.Common.ConvertDate;
import com.example.demo.Common.Enums;
import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.LaptopDTO;
import com.example.demo.DTO.Response.LaptopResponse;
import com.example.demo.Models.Comment;
import com.example.demo.Models.Laptop;
import com.example.demo.Models.LaptopModel;
import com.example.demo.Repository.LaptopQueryRepository;
import com.example.demo.Repository.LaptopRepository;
import com.example.demo.Repository.LaptopModelRepository;
import com.example.demo.Service.LaptopService;

import com.example.demo.mapper.LaptopMapper;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class LaptopServiceImpl implements LaptopService {

    @PersistenceContext
    private EntityManager entityManager;

    private final LaptopQueryRepository laptopQueryRepository;
    private final LaptopRepository laptopRepository;
    private final LaptopModelRepository laptopModelRepository;

    public LaptopServiceImpl(LaptopQueryRepository laptopQueryRepository, LaptopRepository laptopRepository, LaptopModelRepository laptopModelRepository) {
        this.laptopRepository = laptopRepository;
        this.laptopModelRepository = laptopModelRepository;
        this.laptopQueryRepository = laptopQueryRepository;
    }

    // **1. Lấy danh sách tất cả Laptop**
    @Transactional
    @Override
    public List<LaptopDTO> getAllLaptops() {
        return laptopRepository.findAll().stream()
                .map(LaptopMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    // **2. Lấy Laptop chi tiết theo ID**
    @Transactional
    @Override
    public LaptopDTO getLaptopById(UUID id) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laptop not found!"));
        return LaptopMapper.convertToDTO(laptop);
    }

    // **3. Tạo mới Laptop**
    @Transactional
    @Override
    public LaptopDTO createLaptop(LaptopDTO laptopDTO) {
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

        return LaptopMapper.convertToDTO(laptopExisting);
    }

    // **4. Cập nhật Laptop**
    @Override
    @Transactional
    public LaptopDTO updateLaptop(UUID laptopId, LaptopDTO updatedLaptopDTO) {

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
        return LaptopMapper.convertToDTO(laptopExisting);
    }

    @Override
    public LaptopDTO partialUpdateLaptop(UUID id, Map<String, Object> fieldsToUpdate) {
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
        return LaptopMapper.convertToDTO(updatedLaptop);
    }

    // **5. Xóa Laptop theo ID**
    @Transactional
    @Override
    public void deleteLaptop(UUID id) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laptop not found"));

//        LaptopModel laptopModel = laptop.getLaptopModel();
//        laptopModel.removeLaptop(laptop);

        laptopRepository.deleteById(id);
    }

    public List<LaptopResponse> searchLaptops(Map<String, Object> filters) {
        return laptopQueryRepository.searchLaptopsByLaptopModelAndLaptop(filters);

    }





}