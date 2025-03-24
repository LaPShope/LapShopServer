package com.example.demo.Service;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.LaptopDTO;
import com.example.demo.DTO.Response.LaptopResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LaptopService {
    LaptopResponse partialUpdateLaptop(UUID id, Map<String,Object> fieldsToUpdate);

    List<LaptopResponse> getAllLaptops();

    LaptopResponse getLaptopById(UUID id);

    LaptopResponse createLaptop(LaptopDTO laptopDTO);

    LaptopResponse updateLaptop(UUID id, LaptopDTO updatedLaptop);

    List<LaptopResponse> searchLaptopsOnSale();

    void deleteLaptop(UUID id);

    List<LaptopResponse> searchLaptops(Map<String,Object> filters);
}