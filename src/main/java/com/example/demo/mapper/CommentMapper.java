package com.example.demo.mapper;

import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.Response.CommentResponse.CommentItem;
import com.example.demo.DTO.Response.CommentResponse.CommentItems;
import com.example.demo.DTO.Response.CommentResponse.CommentResponse;
import com.example.demo.Models.Comment;

import java.util.Collections;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentDTO convertToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .accountId(comment.getAccount().getId())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .body(comment.getBody())
                .laptopModelId(comment.getLaptopModel().getId())
                .build();
    }

    public static CommentResponse convertToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .account(AccountMapper.convertToDTO(comment.getAccount()))
                .parent(comment.getParent() == null ? null : convertToItem(comment.getParent()))
                .body(comment.getBody())
                .laptopModel(LaptopModelMapper.convertToDTO(comment.getLaptopModel()))
                .replies(comment.getReplies() == null ? Collections.emptyList() : comment.getReplies().stream().map(CommentMapper::convertToItem).collect(Collectors.toList()))
                .build();
    }

    public static CommentItem convertToItem(Comment comment) {
        return CommentItem.builder()
                .id(comment.getId())
                .parent(comment.getParent() == null  ? null : comment.getParent().getId())
                .laptopModelId(comment.getLaptopModel() == null ? null : comment.getLaptopModel().getId())
                .body(comment.getBody())
                .account(comment.getAccount().getId())
                .build();
    }


    public static CommentItems convertToItems(Comment comment) {
        return CommentItems.builder()
                .id(comment.getId())
                .parent(comment.getParent() == null ? null : comment.getParent().getId())
                .laptopModelId(comment.getLaptopModel() == null ? null :comment.getLaptopModel().getId())
                .body(comment.getBody())
                .account(comment.getAccount().getId())
                .replies(comment.getReplies() == null ? Collections.emptyList() : comment.getReplies()
                        .stream()
                        .map(CommentMapper::convertToItems)
                        .collect(Collectors.toList()))
                .build();
    }

}
