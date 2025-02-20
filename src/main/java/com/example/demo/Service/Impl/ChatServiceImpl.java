package com.example.demo.Service.Impl;


import com.example.demo.DTO.CartDTO;
import com.example.demo.DTO.ChatDTO;
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
    public List<ChatDTO> getAllChatsByAccountId(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException("Account not found"));

        List<ChatDTO> chatDTOList = chatRepository.findBySenderId(account).stream()
                .map(ChatMapper::convertToDTO)
                .collect(Collectors.toList());

        chatRepository.findByReceiverId(account).stream()
                .map(ChatMapper::convertToDTO)
                .forEach(chatDTOList::add);

        return chatDTOList;
    }

    @Transactional
    @Override
    public ChatDTO getChatById(UUID id) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with ID: " + id));
        return ChatMapper.convertToDTO(chat);
    }

    @Transactional
    @Override
    public ChatDTO createChat(ChatDTO chatDTO) {
        Account sender = accountRepository.findById(chatDTO.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender Account not found"));
        Account receiver = accountRepository.findById(chatDTO.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver Account not found"));

        Chat chat = Chat.builder()
                .receiverId(receiver)
                .senderId(sender)
                .message(chatDTO.getMessage())
                .createAt(LocalDateTime.now())
                .build();

        Chat chatExisting = chatRepository.save(chat);

        sender.getChatSend().add(chat);
        receiver.getChatReceive().add(chat);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        return ChatMapper.convertToDTO(chatExisting);
    }

    @Transactional
    @Override
    public ChatDTO updateChat(UUID chatId, ChatDTO chatDTO) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        Account sender = accountRepository.findById(chatDTO.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        Account receiver = accountRepository.findById(chatDTO.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        chat.setMessage(chatDTO.getMessage());

        chat.setCreateAt(LocalDateTime.now());

        chatRepository.save(chat);

        return ChatMapper.convertToDTO(chat);
    }

    @Override
    public ChatDTO partialUpdateChat(UUID id, Map<String, Object> fieldsToUpdate) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat with ID " + id + " not found!"));

        Class<?> clazz = chat.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    field.set(chat, newValue);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Chat updatedChat = chatRepository.save(chat);
        return ChatMapper.convertToDTO(updatedChat);
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