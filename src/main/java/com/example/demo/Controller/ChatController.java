package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.ChatDTO;
import com.example.demo.Service.ChatService;
import org.springframework.http.HttpStatus;
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
    @GetMapping("/{accountId}")
    public ResponseEntity<DataResponse<List<ChatDTO>>> getAllChatsByAccountId(@PathVariable UUID accountId) {

            return ResponseEntity.ok(DataResponse.<List<ChatDTO>>builder()
                    .success(true)
                    .message("Chat retrieved successfully")
                    .data(chatService.getAllChatsByAccountId(accountId))
                    .build());
    }


    // Lấy chat theo ID
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getChatById(@PathVariable UUID id) {
//        try {
//            ChatDTO chatDTO = chatService.getChatById(id);
//            return ResponseEntity.ok(chatDTO);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error: Unable to fetch chat. Server encountered an issue.");
//        }
//    }

    // Tạo một chat mới
    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody ChatDTO chatDTO) {

            return ResponseEntity.ok(DataResponse.<ChatDTO>builder()
                    .success(true)
                    .message("Chat created successfully")
                    .data(chatService.createChat(chatDTO))
                    .build());
    }

    // Cập nhật chat
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChat(@PathVariable UUID id, @RequestBody ChatDTO chatDTO) {

        return ResponseEntity.ok(DataResponse.<ChatDTO>builder()
                .success(true)
                .message("Chat updated successfully")
                .data(chatService.updateChat(id, chatDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataResponse<ChatDTO>> partialUpdateChat(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fieldsToUpdate) {

        ChatDTO updatedChat = chatService.partialUpdateChat(id, fieldsToUpdate);

        return ResponseEntity.ok(DataResponse.<ChatDTO>builder()
                .success(true)
                .message("Chat updated successfully!")
                .data(updatedChat)
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