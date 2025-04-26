package com.example.demo.mapper;

import com.example.demo.dto.ChatDTO;
import com.example.demo.dto.response.ChatResponse;
import com.example.demo.model.Chat;

public class ChatMapper {
    public static ChatDTO convertToDTO(Chat chat) {
        return ChatDTO.builder()
                .id(chat.getId())
                .message(chat.getMessage())
                .createAt(chat.getCreateAt())
                .receiverId(chat.getReceiverId().getId())
//                .senderId(chat.getSenderId().getId())
                .build();

    }

    public static ChatResponse convertToResponse(Chat chat) {
        return ChatResponse.builder()
                .id(chat.getId())
                .message(chat.getMessage())
                .createAt(chat.getCreateAt())
                .receiverId(AccountMapper.convertToDTO(chat.getReceiverId()))
                .senderId(AccountMapper.convertToDTO(chat.getSenderId()))
                .build();

    }
}
