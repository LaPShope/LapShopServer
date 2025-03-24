package com.example.demo.service;

import com.example.demo.dto.ChatDTO;
import com.example.demo.dto.response.ChatResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChatService {
    ChatResponse partialUpdateChat(UUID id, Map<String,Object> fieldsToUpdate);

    List<ChatResponse> getAllChatsByAccountId(UUID accountId);

    ChatResponse getChatById(UUID id);

    ChatResponse createChat(ChatDTO chatDTO);

    ChatResponse updateChat(UUID chatId, ChatDTO chatDTO);

    void deleteChat(UUID chatId);
}