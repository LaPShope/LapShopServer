package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private UUID id;
    @JsonProperty("account_id")
    private UUID accountId;
    @JsonProperty("parent_id")
    private UUID parentId;
    private String body;
    @JsonProperty("laptop_model_id")
    private UUID laptopModelId;
    private List<UUID> replies;
}