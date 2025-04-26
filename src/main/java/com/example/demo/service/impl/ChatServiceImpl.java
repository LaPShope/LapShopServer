package com.example.demo.service.impl;


import com.example.demo.common.AuthUtil;
import com.example.demo.common.ConvertDate;
import com.example.demo.common.ConvertSnakeToCamel;
import com.example.demo.dto.ChatDTO;
import com.example.demo.dto.response.ChatResponse;
import com.example.demo.model.Account;
import com.example.demo.model.Chat;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.ChatRepository;
import com.example.demo.service.ChatService;
import com.example.demo.mapper.ChatMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final AccountRepository accountRepository;
    private final  RedisService redisService;

    public ChatServiceImpl(RedisService redisService, ChatRepository chatRepository, AccountRepository accountRepository  ) {
        this.chatRepository = chatRepository;
        this.accountRepository = accountRepository;
        this.redisService = redisService;
    }

    @Override
    public List<ChatResponse> getAllChatsByAccountId() {
        String currentUserEmail = AuthUtil.AuthCheck();

        List<ChatResponse> cachedMessage= redisService.getChatList("allChatAccountId:"+currentUserEmail);
        if (!cachedMessage.isEmpty()) {
            return cachedMessage;
        }
        Account account = accountRepository.findByEmail(currentUserEmail).orElseThrow(() -> new EntityNotFoundException("Account not found"));

        //truong hop la sender
        List<ChatResponse> chatResponsesList = chatRepository.findBySenderId(account).stream()
                .map(ChatMapper::convertToResponse)
                .collect(Collectors.toList());

        //truong hop la receiver
        chatRepository.findByReceiverId(account).stream()
                .map(ChatMapper::convertToResponse)
                .forEach(chatResponsesList::add);

        redisService.saveChatList("allChatAccountId:"+currentUserEmail, chatResponsesList, 1000);

        return chatResponsesList;
    }

    @Override
    public ChatResponse getChatById(UUID id) {
        ChatResponse cachedChat = redisService.getObject("chat:", new TypeReference<ChatResponse>() {});
        if (cachedChat != null) {
            return cachedChat;
        }

        Chat chat = chatRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Chat not found with ID: " + id));

        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(chat.getSenderId().getEmail()) && !currentUserEmail.equals(chat.getReceiverId().getEmail())) {
            throw new SecurityException("User is not authorized to view this chat");
        }

        ChatResponse chatResponse = ChatMapper.convertToResponse(chat);

        redisService.setObject("chat:"+id, chatResponse, 600); // 600 giây = 10 phút

        return chatResponse;
    }

    @Transactional
    @Override
    public ChatResponse createChat(ChatDTO chatDTO) {
        Account receiver = accountRepository.findById(chatDTO.getReceiverId())
        .orElseThrow(() -> new EntityNotFoundException("Receiver Account not found"));

        String currentUserEmail = AuthUtil.AuthCheck();
        
        Account sender = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Sender Account not found"));

        Chat chat = Chat.builder()
                .receiverId(receiver)
                .senderId(sender)
                .message(chatDTO.getMessage())
                .createAt(new Date() )
                .build();

        Chat chatExisting = chatRepository.save(chat);

        sender.getChatSend().add(chat);
        receiver.getChatReceive().add(chat);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        ChatResponse chatResponse = ChatMapper.convertToResponse(chatExisting);

        redisService.deleteByPatterns(List.of("allChatAccountId:"+chat.getSenderId().getEmail(),"allChatAccountId:"+chat.getReceiverId().getEmail(),"chat:"+chat.getId()));

        return chatResponse;
    }

    @Transactional
    @Override
    public ChatResponse updateChat(UUID chatId, ChatDTO chatDTO) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        
        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(chat.getSenderId().getEmail()) && !currentUserEmail.equals(chat.getReceiverId().getEmail())){
            throw new SecurityException("User is not authorized to update this chat");
        }
        chat.setMessage(chatDTO.getMessage());

        chat.setCreateAt(new Date());

        chatRepository.save(chat);

        ChatResponse cachedChat = ChatMapper.convertToResponse(chat);

        redisService.deleteByPatterns(List.of("allChatAccountId:"+chat.getSenderId().getEmail(),"allChatAccountId:"+chat.getReceiverId().getEmail(),"chat:"+chat.getId()));

        return cachedChat;
    }

    @Transactional
    @Override
    public ChatResponse partialUpdateChat(UUID id, Map<String, Object> fieldsToUpdate) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat with ID " + id + " not found!"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(chat.getSenderId().getEmail()) && !currentUserEmail.equals(chat.getReceiverId().getEmail())){
            throw new SecurityException("User is not authorized to update this chat");
        }
        
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

        ChatResponse cachedChat = ChatMapper.convertToResponse(updatedChat);

        redisService.deleteByPatterns(List.of("allChatAccountId:"+chat.getSenderId().getEmail(),"allChatAccountId:"+chat.getReceiverId().getEmail(),"chat:"+chat.getId()));


        return cachedChat;
    }

    @Transactional
    @Override
    public void deleteChat(UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        Account sender = chat.getSenderId();
        Account receiver = chat.getReceiverId();

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(sender.getEmail()) && !currentUserEmail.equals(receiver.getEmail())){
            throw new SecurityException("User is not authorized to delete this chat");
        }

        sender.getChatSend().remove(chat);
        receiver.getChatReceive().remove(chat);

        redisService.deleteByPatterns(List.of("allChatAccountId:"+chat.getSenderId().getEmail(),"allChatAccountId:"+chat.getReceiverId().getEmail(),"chat:"+chat.getId()));


        chatRepository.delete(chat);
    }


}