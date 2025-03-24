package com.example.demo.Service;

import com.example.demo.DTO.LaptopDTO;
import com.example.demo.DTO.LaptopOnCartDTO;
import com.example.demo.DTO.Response.LaptopOnCartResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LaptopOnCartService {
    LaptopOnCartResponse partialUpdateLaptopOnCart(UUID id, Map<String,Object> fieldsToUpdate);

    List<LaptopOnCartResponse> getAllLaptopOnCarts();

    LaptopOnCartResponse getLaptopOnCartById(UUID id);

    LaptopOnCartResponse createLaptopOnCart(LaptopOnCartDTO laptopOnCartDTO);

    LaptopOnCartResponse updateLaptopOnCart(UUID id, LaptopOnCartDTO laptopOnCartDTO);

    void deleteLaptopOnCart(UUID id);
}