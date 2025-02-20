package com.example.demo.Service;

import com.example.demo.DTO.PaymentDTO;
import com.example.demo.DTO.SaleDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SaleService {
    SaleDTO partialUpdateSale(UUID id, Map<String,Object> fieldsToUpdate);

    List<SaleDTO> getAllSales();

    SaleDTO getSaleById(UUID id);

    SaleDTO createSale(SaleDTO saleDTO);

    SaleDTO updateSale(UUID saleId, SaleDTO saleDTO);

    void deleteSale(UUID id);
}