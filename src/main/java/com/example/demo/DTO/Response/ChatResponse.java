package com.example.demo.DTO.Response;

import com.example.demo.DTO.AccountDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private UUID id;
    @JsonProperty("sender_id")
    private AccountDTO senderId;
    @JsonProperty("receiver_id")
    private AccountDTO receiverId;
    private String message;
    @JsonProperty("create_at")
    private Date createAt;

}
