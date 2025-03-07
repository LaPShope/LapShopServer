package com.example.demo.DTO.Response.CommentResponse;

import com.example.demo.DTO.AccountDTO;
import com.example.demo.DTO.LaptopModelDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentItem {
    private UUID id;

    private UUID account;

    private UUID parent;

    private String body;

    @JsonProperty("laptop_model_id")
    private UUID laptopModelId;

}
