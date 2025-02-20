package com.example.demo.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Builder
@Data
public class ImageDTO {
    private UUID id;
    private String imageUrl;
    private List<UUID> laptopModelIds;
}