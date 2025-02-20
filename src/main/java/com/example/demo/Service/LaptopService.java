package com.example.demo.Service;

import com.example.demo.DTO.LaptopDTO;

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
}