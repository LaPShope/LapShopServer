package com.example.demo.Service;

import com.example.demo.DTO.ChatDTO;
import com.example.demo.DTO.CommentDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CommentService {
    CommentDTO partialUpdateComment(UUID id, Map<String,Object> fieldsToUpdate);
    List<CommentDTO> getAllCommentsByAccountId(UUID accountId);
    CommentDTO getCommentById(UUID id);
    CommentDTO createComment(CommentDTO commentDTO);
    CommentDTO updateComment(UUID id, CommentDTO updatedComment);
    void deleteComment(UUID id);
}