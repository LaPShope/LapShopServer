package com.example.demo.dto;

import com.example.demo.common.Enums;
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
    private String brand;
    private String cpu;
    private String ram;
    private String gpu;
    private String storage;
    private String display;
    private Enums.Color color;
    private BigDecimal price;
    private String description;
    private List<ImageDTO> images;
//    @JsonProperty("comment_ids")
//    private List<UUID> commentIds;
//    @JsonProperty("image_ids")
//    private List<UUID> imageIds;
//    @JsonProperty("sale_ids")
//    private List<UUID> saleIds;
//    @JsonProperty("laptop_ids")
//    private List<UUID> laptopIds;
//    @JsonProperty("laptop_on_cart_ids")
//    private List<UUID> laptopOnCartIds;
//    @JsonProperty("oder_detail_ids")
//    private List<UUID> orderDetailIds;
}