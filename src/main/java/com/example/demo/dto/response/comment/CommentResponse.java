package com.example.demo.dto.response.comment;

import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.LaptopModelDTO;
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
public class CommentResponse {
    private UUID id;

    private AccountDTO account;

    private CommentItem parent;

    private String body;

    @JsonProperty("laptop_model")
    private LaptopModelDTO laptopModel;

    private List<CommentItem> replies;
}
