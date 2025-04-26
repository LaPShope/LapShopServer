package com.example.demo.service;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.response.comment.CommentResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CommentService {
    CommentResponse partialUpdateComment(UUID id, Map<String,Object> fieldsToUpdate);
    List<CommentResponse> getAllCommentsByAccount();
    List<CommentResponse> getAllCommentsByLaptopModelId(UUID laptopModelId);
    CommentResponse getCommentById(UUID id);
    CommentResponse createComment(CommentDTO commentDTO);
    CommentResponse updateComment(UUID id, CommentDTO updatedComment);
    void deleteComment(UUID id);
}