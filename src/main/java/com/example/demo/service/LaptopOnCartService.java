package com.example.demo.service;

import com.example.demo.dto.LaptopOnCartDTO;
import com.example.demo.dto.response.LaptopOnCartResponse;

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