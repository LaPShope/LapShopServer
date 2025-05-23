package com.example.demo.service.impl;

import com.example.demo.common.AuthUtil;
import com.example.demo.common.ConvertDate;
import com.example.demo.common.ConvertSnakeToCamel;
import com.example.demo.dto.response.SaleResponse;
import com.example.demo.dto.SaleDTO;
import com.example.demo.model.LaptopModel;
import com.example.demo.model.Sale;
import com.example.demo.repository.LaptopModelRepository;
import com.example.demo.repository.SaleRepository;
import com.example.demo.service.SaleService;

import com.example.demo.mapper.SaleMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleServiceImpl implements SaleService {
    private final RedisService redisService;
    private final SaleRepository saleRepository;
    private final LaptopModelRepository laptopModelRepository;

    public SaleServiceImpl(RedisService redisService, SaleRepository saleRepository, LaptopModelRepository laptopModelRepository) {
        this.saleRepository = saleRepository;
        this.laptopModelRepository = laptopModelRepository;
        this.redisService = redisService;
    }

    @Override
    public List<SaleResponse> getAllSales() {
        List<SaleResponse> cachedSales = redisService.getObject("allSale", new TypeReference<List<SaleResponse>>() {});
        if(cachedSales != null && !cachedSales.isEmpty()){
            return cachedSales;
        }

        List<SaleResponse> saleResponses = saleRepository.findAll().stream()
                .map(SaleMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allSale",saleResponses,600);

        return saleResponses;
    }

    @Override
    public SaleResponse getSaleById(UUID id) {
        SaleResponse cachedSale = redisService.getObject("allSale", new TypeReference<SaleResponse>() {});
        if(cachedSale != null){
            return cachedSale;
        }
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with ID: " + id));
        SaleResponse saleResponse = SaleMapper.convertToResponse(sale);

        redisService.setObject("sale:"+id,saleResponse,600);

        return saleResponse;
    }

    //Tạo mới một Sale
    @Transactional
    @Override
    public SaleResponse createSale(SaleDTO saleDTO) {
        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

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

        SaleResponse cachedSale = SaleMapper.convertToResponse(sale);

        redisService.deleteByPatterns(List.of("allSale","*aptopModel*","allLaptopOnSale"));
        redisService.setObject("sale:"+cachedSale.getId(),cachedSale,600);

        return cachedSale;
    }

    //cap nhat sale
    @Transactional
    @Override
    public SaleResponse updateSale(UUID saleId, SaleDTO saleDTO) {
        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found"));

        sale.setId(saleId);
        sale.setDiscount(saleDTO.getDiscount());
        sale.setEndAt(saleDTO.getStartAt());
        sale.setEndAt(saleDTO.getEndAt());
        sale.setEvent_description(saleDTO.getEventDescription());

        if (saleDTO.getLaptopModelIds() != null) {
            List<LaptopModel> laptopModels = saleDTO.getLaptopModelIds().stream()
                    .map(laptopModelId -> laptopModelRepository.findById(laptopModelId)
                            .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found")))
                    .collect(Collectors.toList());
            sale.setLaptopModelList(laptopModels);
        }

        Sale saleExisting = saleRepository.save(sale);
        SaleResponse cachedSale = SaleMapper.convertToResponse(saleExisting);

        redisService.deleteByPatterns(List.of("allSale","*aptopModel*","allLaptopOnSale"));
        redisService.setObject("sale:id"+saleId,cachedSale,600);

        return cachedSale;
    }

    @Transactional
    @Override
    public SaleResponse partialUpdateSale(UUID id, Map<String, Object> fieldsToUpdate) {
        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale with ID " + id + " not found!"));

        Class<?> clazz = sale.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldName.equals("start_at") || fieldName.equals("end_at")){
                fieldName= ConvertSnakeToCamel.snakeToCamel(fieldName);
            }
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    if (field.getType().equals(Date.class)) {
                        Date parsedDate = ConvertDate.convertToDate(newValue);
                        field.set(sale,parsedDate );
                    } else if (field.getType().equals(Float.class)) {
                        field.set(sale, Float.parseFloat(newValue.toString()));
                    }  else {
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
        SaleResponse cachedSale = SaleMapper.convertToResponse(updatedSale);

        redisService.deleteByPatterns(List.of("allSale","*aptopModel*","allLaptopOnSale"));
        redisService.setObject("sale:id"+id,cachedSale,600);

        return cachedSale;
    }


    //Xóa Sale theo ID
    @Transactional
    @Override
    public void deleteSale(UUID id) {
        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found"));

        sale.getLaptopModelList().forEach(laptopModel -> laptopModel.getSaleList().remove(sale));

        redisService.deleteByPatterns(List.of("allSale","*aptopModel*","allLaptopOnSale"));

        saleRepository.delete(sale);
    }


}