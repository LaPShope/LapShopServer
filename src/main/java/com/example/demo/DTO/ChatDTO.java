package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDTO {
    private UUID id;
    @JsonProperty("sender_id")
    private UUID senderId;
    @JsonProperty("receiver_id")
    private UUID receiverId;
    private String message;
    @JsonProperty("create_at")
    private Date createAt;
}