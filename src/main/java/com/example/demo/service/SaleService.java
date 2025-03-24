package com.example.demo.service;

import com.example.demo.dto.response.SaleResponse;
import com.example.demo.dto.SaleDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SaleService {
    SaleResponse partialUpdateSale(UUID id, Map<String,Object> fieldsToUpdate);

    List<SaleResponse> getAllSales();

    SaleResponse getSaleById(UUID id);

    SaleResponse createSale(SaleDTO saleDTO);

    SaleResponse updateSale(UUID saleId, SaleDTO saleDTO);

    void deleteSale(UUID id);
}