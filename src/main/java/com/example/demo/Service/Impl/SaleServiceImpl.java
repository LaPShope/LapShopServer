package com.example.demo.Service.Impl;

import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.SaleDTO;
import com.example.demo.Models.Comment;
import com.example.demo.Models.LaptopModel;
import com.example.demo.Models.Sale;
import com.example.demo.Repository.LaptopModelRepository;
import com.example.demo.Repository.SaleRepository;
import com.example.demo.Service.SaleService;

import com.example.demo.mapper.SaleMapper;
import jakarta.persistence.Column;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final LaptopModelRepository laptopModelRepository;

    public SaleServiceImpl(SaleRepository saleRepository, LaptopModelRepository laptopModelRepository) {
        this.saleRepository = saleRepository;
        this.laptopModelRepository = laptopModelRepository;
    }

    //Lấy danh sách tất cả Sale
    @Transactional
    @Override
    public List<SaleDTO> getAllSales() {
        return saleRepository.findAll().stream()
                .map(SaleMapper::convertToDTO)
                .collect(Collectors.toList());

    }

    //Lấy Sale theo ID
    @Transactional
    @Override
    public SaleDTO getSaleById(UUID id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with ID: " + id));
        return SaleMapper.convertToDTO(sale);
    }

    //Tạo mới một Sale
    @Transactional
    @Override
    public SaleDTO createSale(SaleDTO saleDTO) {
        Sale sale = Sale.builder()
                .id(null)
                .event_description(saleDTO.getEventDescription())
                .startAt(saleDTO.getStartAt())
                .endAt(saleDTO.getEndAt())
                .discount(saleDTO.getDiscount())
                .build();

        if (saleDTO.getLaptopModelIds() != null && !saleDTO.getLaptopModelIds().isEmpty()) {
            List<LaptopModel> laptopModels = saleDTO.getLaptopModelIds().stream()
                    .map(laptopModelId -> laptopModelRepository.findById(laptopModelId)
                            .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found ")))
                    .collect(Collectors.toList());
            sale.setLaptopModelList(laptopModels);
        }
        Sale saleExisting = saleRepository.save(sale);

        return SaleMapper.convertToDTO(sale);
    }

    //cap nhat sale
    @Transactional
    @Override
    public SaleDTO updateSale(UUID saleId, SaleDTO saleDTO) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found"));

        sale.setId(saleId);
        sale.setDiscount(saleDTO.getDiscount());
        sale.setEndAt(saleDTO.getStartAt());
        sale.setEvent_description(saleDTO.getEventDescription());

        if (saleDTO.getLaptopModelIds() != null) {
            List<LaptopModel> laptopModels = saleDTO.getLaptopModelIds().stream()
                    .map(laptopModelId -> laptopModelRepository.findById(laptopModelId)
                            .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found")))
                    .collect(Collectors.toList());
            sale.setLaptopModelList(laptopModels);
        }

        Sale saleExisting = saleRepository.save(sale);
        return SaleMapper.convertToDTO(saleExisting);
    }

    @Transactional
    @Override
    public SaleDTO partialUpdateSale(UUID id, Map<String, Object> fieldsToUpdate) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale with ID " + id + " not found!"));

        Class<?> clazz = sale.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    if (field.getType().equals(Date.class)) {
                        field.set(sale, new Date((Long) newValue)); // Chuyển timestamp thành `Date`
                    } else if (field.getType().equals(Float.class)) {
                        field.set(sale, Float.parseFloat(newValue.toString()));
                    } else if (field.getType().equals(List.class) && fieldName.equals("laptopModelList")) {
                        List<UUID> laptopModelIds = (List<UUID>) newValue;
                        List<LaptopModel> laptopModels = laptopModelRepository.findAllById(laptopModelIds);
                        field.set(sale, laptopModels);
                    } else {
                        field.set(sale, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Sale updatedSale = saleRepository.save(sale);
        return SaleMapper.convertToDTO(updatedSale);
    }


    //Xóa Sale theo ID
    @Transactional
    @Override
    public void deleteSale(UUID id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found"));

        sale.getLaptopModelList().forEach(laptopModel -> laptopModel.getSaleList().remove(sale));

        saleRepository.delete(sale);
    }


}