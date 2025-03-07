package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Builder
@Data
public class ImageDTO {
    private UUID id;
    private String imageUrl;
    @JsonProperty("laptop_model_ids")
    private List<UUID> laptopModelIds;
}