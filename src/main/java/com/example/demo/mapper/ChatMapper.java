package com.example.demo.mapper;

import com.example.demo.DTO.ChatDTO;
import com.example.demo.Models.Chat;

public class ChatMapper {
    public static ChatDTO convertToDTO(Chat chat) {
        return ChatDTO.builder()
                .id(chat.getId())
                .message(chat.getMessage())
                .createAt(chat.getCreateAt())
                .receiverId(chat.getReceiverId().getId())
                .senderId(chat.getSenderId().getId())
                .build();

    }
}
