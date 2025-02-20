package com.example.demo.mapper;

import com.example.demo.DTO.LaptopDTO;
import com.example.demo.Models.Laptop;

public class LaptopMapper {
    public static LaptopDTO convertToDTO(Laptop laptop) {
        return LaptopDTO.builder()
                .macId(laptop.getMacId())
                .status(laptop.getStatus())
                .laptopModelId(laptop.getLaptopModel() == null ? null
                        : laptop.getLaptopModel().getId())
                .mfg(laptop.getMFG())
                .build();
    }
}
