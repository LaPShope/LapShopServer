package com.example.demo.service;

import com.example.demo.dto.LaptopDTO;
import com.example.demo.dto.response.LaptopResponse;
import com.example.demo.dto.response.PagingResponse;

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

    PagingResponse<?> getLaptopsWithPaginationAndSortByPriceASC(double price,int offset, int pageSize);

    PagingResponse<?> getLaptopsWithPaginationAndSortByPriceDES(double price,int offset, int pageSize);

    PagingResponse<?> getLaptopsWithPagination(int offset, int pageSize);
}