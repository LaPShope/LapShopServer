package com.example.demo.mapper;

import com.example.demo.DTO.CommentDTO;
import com.example.demo.Models.Comment;

import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentDTO convertToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .accountId(comment.getAccount().getId())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .body(comment.getBody())
                .laptopModelId(comment.getLaptopModel().getId())
                .replies(comment.getReplies() == null ? null :
                        comment.getReplies().stream()
                                .map(Comment :: getId)
                                .collect(Collectors.toList()))
                .build();
    }

}
