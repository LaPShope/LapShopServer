package com.example.demo.dto.response;

import com.example.demo.common.Enums;
import com.example.demo.dto.ImageDTO;
import com.example.demo.dto.response.comment.CommentItems;
import com.example.demo.dto.SaleDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LaptopModelResponse {
    private UUID id;
    private String name;
    private String brand;
    private String cpu;
    private String ram;
    private String storage;
    private String display;
    private Enums.Color color;
    private String description;
    private String gpu;
    private BigDecimal price;
    @JsonProperty("comment_list")
    private List<CommentItems> commentList;
    @JsonProperty("image_list")
    private List<ImageDTO> imageList;
    @JsonProperty("sale_list")
    private List<SaleDTO> saleList;
//    @JsonProperty("laptop_ids")
//    private List<UUID> laptopIds;
//    @JsonProperty("laptop_on_cart_ids")
//    private List<UUID> laptopOnCartIds;
//    @JsonProperty("oder_detail_ids")
//    private List<UUID> orderDetailIds;
}
