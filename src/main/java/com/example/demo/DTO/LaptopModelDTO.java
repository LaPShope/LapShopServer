package com.example.demo.DTO;

import com.example.demo.Common.Enums;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LaptopModelDTO {
    private UUID id;                     
    private String name;
    private String branch;
    private String cpu;
    private String ram;
    private String storage;
    private String display;
    private Enums.Color color;
    private BigDecimal price;
    private String description;
    private List<UUID> commentIds;
    private List<UUID> imageIds;
    private List<UUID> saleIds;
    private List<UUID> laptopIds;
    private List<UUID> laptopOnCartIds;
    private List<UUID> orderDetailIds;
}