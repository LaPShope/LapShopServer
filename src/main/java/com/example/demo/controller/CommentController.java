package com.example.demo.controller;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.response.comment.CommentResponse;
import com.example.demo.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Lấy danh sách tất cả Comments
    @GetMapping()
    public ResponseEntity<?> getAllComments() {
        return ResponseEntity.ok(DataResponse.<List<CommentResponse>>builder()
                .success(true)
                .message("Comment retrieved successfully")
                .data(commentService.getAllCommentsByAccount())
                .build());
    }

    // Lấy Comment theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable UUID id) {
        return ResponseEntity.ok(DataResponse.<CommentResponse>builder()
                .success(true)
                .message("Comment retrieved successfully")
                .data(commentService.getCommentById(id))
                .build());
    }

    // 3. Tạo mới một Comment
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentDTO commentDTO) {

        return ResponseEntity.ok(DataResponse.<CommentResponse>builder()
                .success(true)
                .message("Comment created successfully")
                .data(commentService.createComment(commentDTO))
                .build());
    }

    // Cập nhật một Comment
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable UUID id, @RequestBody CommentDTO commentDTO) {

        return ResponseEntity.ok(DataResponse.<CommentResponse>builder()
                .success(true)
                .message("Comment updated successfully")
                .data(commentService.updateComment(id,commentDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateComment(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<CommentResponse>builder()
                .success(true)
                .message("Comment updated successfully")
                .data(commentService.partialUpdateComment(id, fieldsToUpdate))
                .build());
    }

    // 5. Xóa một Comment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Comment deleted successfully")
                .build());
    }
}