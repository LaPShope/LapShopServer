package com.example.demo.Service;

import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.Response.CommentResponse.CommentResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CommentService {
    CommentResponse partialUpdateComment(UUID id, Map<String,Object> fieldsToUpdate);
    List<CommentResponse> getAllCommentsByAccountId(UUID accountId);
    CommentResponse getCommentById(UUID id);
    CommentResponse createComment(CommentDTO commentDTO);
    CommentResponse updateComment(UUID id, CommentDTO updatedComment);
    void deleteComment(UUID id);
}