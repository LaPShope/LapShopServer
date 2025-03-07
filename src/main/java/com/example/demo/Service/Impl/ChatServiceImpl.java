package com.example.demo.Service.Impl;


import com.example.demo.Common.ConvertDate;
import com.example.demo.Common.ConvertSnakeToCamel;
import com.example.demo.DTO.CartDTO;
import com.example.demo.DTO.ChatDTO;
import com.example.demo.DTO.Response.ChatResponse;
import com.example.demo.Models.Account;
import com.example.demo.Models.Cart;
import com.example.demo.Models.Chat;
import com.example.demo.Models.LaptopOnCart;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.ChatRepository;
import com.example.demo.Service.ChatService;
import com.example.demo.mapper.ChatMapper;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final AccountRepository accountRepository;

    public ChatServiceImpl(ChatRepository chatRepository, AccountRepository accountRepository  ) {
        this.chatRepository = chatRepository;
        this.accountRepository = accountRepository;

    }

    @Transactional
    @Override
    public List<ChatResponse> getAllChatsByAccountId(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException("Account not found"));

        List<ChatResponse> chatResponsesList = chatRepository.findBySenderId(account).stream()
                .map(ChatMapper::convertToResponse)
                .collect(Collectors.toList());

        chatRepository.findByReceiverId(account).stream()
                .map(ChatMapper::convertToResponse)
                .forEach(chatResponsesList::add);

        return chatResponsesList;
    }

    @Transactional
    @Override
    public ChatResponse getChatById(UUID id) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with ID: " + id));
        return ChatMapper.convertToResponse(chat);
    }

    @Transactional
    @Override
    public ChatResponse createChat(ChatDTO chatDTO) {
        Account sender = accountRepository.findById(chatDTO.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender Account not found"));
        Account receiver = accountRepository.findById(chatDTO.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver Account not found"));

        Chat chat = Chat.builder()
                .receiverId(receiver)
                .senderId(sender)
                .message(chatDTO.getMessage())
                .createAt(new Date())
                .build();

        Chat chatExisting = chatRepository.save(chat);

        sender.getChatSend().add(chat);
        receiver.getChatReceive().add(chat);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        return ChatMapper.convertToResponse(chatExisting);
    }

    @Transactional
    @Override
    public ChatResponse updateChat(UUID chatId, ChatDTO chatDTO) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        Account sender = accountRepository.findById(chatDTO.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        Account receiver = accountRepository.findById(chatDTO.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        chat.setMessage(chatDTO.getMessage());

        chat.setCreateAt(new Date());

        chatRepository.save(chat);

        return ChatMapper.convertToResponse(chat);
    }

    @Override
    public ChatResponse partialUpdateChat(UUID id, Map<String, Object> fieldsToUpdate) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat with ID " + id + " not found!"));

        Class<?> clazz = chat.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            if(fieldName.equals("create_at")){
                fieldName= ConvertSnakeToCamel.snakeToCamel(fieldName);
            }
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    if(field.getType().equals(Date.class)){
                        Date parsedDate = ConvertDate.convertToDate(newValue);
                        field.set(chat, parsedDate);
                    }else{
                        field.set(chat, newValue);
                    }

                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Chat updatedChat = chatRepository.save(chat);
        return ChatMapper.convertToResponse(updatedChat);
    }

    @Transactional
    @Override
    public void deleteChat(UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        Account sender = chat.getSenderId();
        Account receiver = chat.getReceiverId();

        sender.getChatSend().remove(chat);
        receiver.getChatReceive().remove(chat);

        chatRepository.delete(chat);
    }


}