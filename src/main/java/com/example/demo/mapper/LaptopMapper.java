package com.example.demo.mapper;

import com.example.demo.dto.LaptopDTO;
import com.example.demo.dto.response.LaptopResponse;
import com.example.demo.model.Laptop;

public class LaptopMapper {
    public static LaptopDTO convertToDTO(Laptop laptop) {
        return LaptopDTO.builder()
                .macId(laptop.getMacId())
                .status(laptop.getStatus())
                .laptopModelId(laptop.getLaptopModel() == null ? null
                        : laptop.getLaptopModel().getId())
                .MFG(laptop.getMFG())
                .build();
    }

    public static LaptopResponse convertToResponse(Laptop laptop) {
        return LaptopResponse.builder()
                .macId(laptop.getMacId())
                .status(laptop.getStatus())
                .laptopModel(laptop.getLaptopModel() == null ? null
                        : LaptopModelMapper.convertToDTO(laptop.getLaptopModel()))
                .MFG(laptop.getMFG())
                .build();
    }
}
