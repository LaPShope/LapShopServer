package com.example.demo.dto.request.laptopmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
public class AddImagesToLaptopModelRequest {
    @JsonProperty("laptop_model_id")
    private UUID laptopModelId;

    @JsonProperty("image_ids")
    private List<UUID> imageIds;
}
