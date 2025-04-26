package com.example.demo.controller;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.ChatDTO;
import com.example.demo.dto.response.ChatResponse;
import com.example.demo.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Lấy tất cả chat
    @GetMapping()
    public ResponseEntity<DataResponse<List<ChatResponse>>> getAllChatsByAccountId() {

            return ResponseEntity.ok(DataResponse.<List<ChatResponse>>builder()
                    .success(true)
                    .message("Chat retrieved successfully")
                    .data(chatService.getAllChatsByAccountId())
                    .build());
    }


    // Lấy chat theo ID
   @GetMapping("/{id}")
   public ResponseEntity<?> getChatById(@PathVariable UUID id) {
         return ResponseEntity.ok(DataResponse.<ChatResponse>builder()
                .success(true)
                .message("Chat retrieved successfully")
                .data(chatService.getChatById(id))
                .build());
   }

    // Tạo một chat mới
    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody ChatDTO chatDTO) {

            return ResponseEntity.ok(DataResponse.<ChatResponse>builder()
                    .success(true)
                    .message("Chat created successfully")
                    .data(chatService.createChat(chatDTO))
                    .build());
    }

    // Cập nhật chat
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChat(@PathVariable UUID id, @RequestBody ChatDTO chatDTO) {

        return ResponseEntity.ok(DataResponse.<ChatResponse>builder()
                .success(true)
                .message("Chat updated successfully")
                .data(chatService.updateChat(id, chatDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataResponse<ChatResponse>> partialUpdateChat(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fieldsToUpdate) {

        return ResponseEntity.ok(DataResponse.<ChatResponse>builder()
                .success(true)
                .message("Chat updated successfully!")
                .data(chatService.partialUpdateChat(id, fieldsToUpdate))
                .build());
    }


    // Xóa chat
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable UUID id) {
            chatService.deleteChat(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Chat deleted successfully")
                .build());
    }
}