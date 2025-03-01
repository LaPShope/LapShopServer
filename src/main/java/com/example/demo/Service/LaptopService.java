package com.example.demo.Service;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.LaptopDTO;
import com.example.demo.DTO.Response.LaptopResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LaptopService {
    LaptopDTO partialUpdateLaptop(UUID id, Map<String,Object> fieldsToUpdate);

    List<LaptopDTO> getAllLaptops();

    LaptopDTO getLaptopById(UUID id);

    LaptopDTO createLaptop(LaptopDTO laptopDTO);

    LaptopDTO updateLaptop(UUID id, LaptopDTO updatedLaptop);

    void deleteLaptop(UUID id);

    List<LaptopResponse> searchLaptops(Map<String,Object> filters);
}