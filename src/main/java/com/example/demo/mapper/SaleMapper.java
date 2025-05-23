package com.example.demo.mapper;

import com.example.demo.dto.response.SaleResponse;
import com.example.demo.dto.SaleDTO;
import com.example.demo.model.Sale;

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
//                .laptopModelIds(sale.getLaptopModelList() == null ? Collections.emptyList() :
//                        sale.getLaptopModelList().stream()
//                                .map(LaptopModel::getId)
//                                .collect(Collectors.toList()))
                .build();
    }

    public static SaleResponse convertToResponse(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .discount(sale.getDiscount())
                .endAt(sale.getEndAt())
                .startAt(sale.getStartAt())
                .eventDescription(sale.getEvent_description() == null ? null : sale.getEvent_description())
                .laptopModelList(sale.getLaptopModelList() == null ? Collections.emptyList() :
                        sale.getLaptopModelList().stream()
                                .map(LaptopModelMapper::convertToDTO)
                                .collect(Collectors.toList()))
                .build();
    }
}
