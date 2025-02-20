package com.example.demo.DTO;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private UUID id;
    private UUID accountId;
    private UUID parentId;
    private String body;
    private UUID laptopModelId;
    private List<UUID> replies;
}