package com.example.demo.mapper;

import com.example.demo.DTO.SaleDTO;
import com.example.demo.Models.LaptopModel;
import com.example.demo.Models.Sale;

import java.util.Collections;
import java.util.stream.Collectors;

public class SaleMapper {
    public static SaleDTO convertToDTO(Sale sale) {
        return SaleDTO.builder()
                .id(sale.getId())
                .discount(sale.getDiscount())
                .endAt(sale.getEndAt())
                .startAt(sale.getStartAt())
                .eventDescription(sale.getEvent_description() == null ? null : sale.getEvent_description())
                .laptopModelIds(sale.getLaptopModelList() == null ? Collections.emptyList() :
                        sale.getLaptopModelList().stream()
                                .map(LaptopModel::getId)
                                .collect(Collectors.toList()))
                .build();
    }
}
