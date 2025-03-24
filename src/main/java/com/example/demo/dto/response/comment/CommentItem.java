package com.example.demo.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
