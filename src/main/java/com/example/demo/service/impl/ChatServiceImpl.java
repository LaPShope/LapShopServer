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
    public List<ChatResponse> getAllChatsByAccountId(UUID accountId) {

        List<ChatResponse> cachedMessages = redisService.getChatList("allChatAccountId:"+accountId);
        if (!cachedMessages.isEmpty()) {
            return cachedMessages;
        }

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException("Account not found"));

        List<ChatResponse> chatResponsesList = chatRepository.findBySenderId(account).stream()
                .map(ChatMapper::convertToResponse)
                .collect(Collectors.toList());

        chatRepository.findByReceiverId(account).stream()
                .map(ChatMapper::convertToResponse)
                .forEach(chatResponsesList::add);

        redisService.saveChatList("allChatAccountId:"+accountId, chatResponsesList, 1000);

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

        ChatResponse chatResponse = ChatMapper.convertToResponse(chat);

        redisService.setObject("chat:"+id, chatResponse, 600); // 600 giây = 10 phút

        return chatResponse;
    }

    @Transactional
    @Override
    public ChatResponse createChat(ChatDTO chatDTO) {
        Account sender = accountRepository.findById(chatDTO.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender Account not found"));
        Account receiver = accountRepository.findById(chatDTO.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver Account not found"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(sender.getEmail())){
            throw new SecurityException("User is not authorized to create this chat");
        }

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

        List<ChatResponse> senderChatList = redisService.getChatList("allChatAccountId:"+chatDTO.getSenderId());
        List<ChatResponse> receiverChatList = redisService.getChatList("allCChatAccountId:"+chatDTO.getReceiverId());

        senderChatList.add(chatResponse);
        receiverChatList.add(chatResponse);

        // redisService.saveChatList("allChatAccountId:"+chatDTO.getSenderId(), senderChatList, 600);
        // redisService.saveChatList("allCChatAccountId:"+chatDTO.getReceiverId(), receiverChatList, 600);

        return chatResponse;
    }

    @Transactional
    @Override
    public ChatResponse updateChat(UUID chatId, ChatDTO chatDTO) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        
        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(chat.getSenderId().getEmail())){
            throw new SecurityException("User is not authorized to update this chat");
        }
        
        Account sender = accountRepository.findById(chatDTO.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        Account receiver = accountRepository.findById(chatDTO.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        chat.setMessage(chatDTO.getMessage());

        chat.setCreateAt(new Date());

        chatRepository.save(chat);

        ChatResponse cachedChat = ChatMapper.convertToResponse(chat);

        // updateChatInRedis("allChatAccountId:"+chatDTO.getReceiverId(), chatId, cachedChat);
        // updateChatInRedis("allChatAccountId:"+chatDTO.getSenderId(), chatId, cachedChat);

        return cachedChat;
    }

    @Transactional
    @Override
    public ChatResponse partialUpdateChat(UUID id, Map<String, Object> fieldsToUpdate) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat with ID " + id + " not found!"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(chat.getSenderId().getEmail())){
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

        // updateChatInRedis("allChatAccountId:" + chat.getReceiverId().getId(), id, cachedChat);
        // updateChatInRedis("allChatAccountId:" + chat.getSenderId().getId(), id, cachedChat);

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
        if(!currentUserEmail.equals(sender.getEmail())){
            throw new SecurityException("User is not authorized to delete this chat");
        }

        sender.getChatSend().remove(chat);
        receiver.getChatReceive().remove(chat);

        redisService.deleteByPatterns(List.of("allChatAccount:"+chat.getSenderId().getId(),"allChatAccount:"+chat.getReceiverId().getId()));

        chatRepository.delete(chat);
    }

    // private void updateChatInRedis(String redisKey, UUID chatId, ChatResponse updatedChatResponse) {
    //     List<ChatResponse> chatList = redisService.getChatList(redisKey);

    //     if (chatList == null || chatList.isEmpty()) {
    //         return;
    //     }

    //     for (int i = 0; i < chatList.size(); i++) {
    //         if (chatList.get(i).getId().equals(chatId)) {
    //             chatList.set(i, updatedChatResponse); // Cập nhật tin nhắn
    //             redisService.saveChatList(redisKey, chatList, 20); // Lưu lại vào Redis
    //             return;
    //         }
    //     }
    // }


}