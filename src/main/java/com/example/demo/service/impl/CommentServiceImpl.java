package com.example.demo.service.impl;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.response.comment.CommentResponse;
import com.example.demo.model.Comment;
import com.example.demo.model.Account;
import com.example.demo.model.LaptopModel;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.LaptopModelRepository;
import com.example.demo.service.CommentService;
import com.example.demo.mapper.CommentMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final RedisService redisService;
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final LaptopModelRepository laptopModelRepository;

    public CommentServiceImpl(RedisService redisService, CommentRepository commentRepository, AccountRepository accountRepository, LaptopModelRepository laptopModelRepository) {
        this.commentRepository = commentRepository;
        this.accountRepository = accountRepository;
        this.laptopModelRepository = laptopModelRepository;
        this.redisService = redisService;
    }

    // Lấy danh sách tất cả Comment
    @Transactional
    @Override
    public List<CommentResponse> getAllCommentsByAccountId(UUID accountId) {
        List<CommentResponse> cachedCommentResponses = redisService.getObject("allComment", new TypeReference<List<CommentResponse>>() {});
        if(cachedCommentResponses != null && !cachedCommentResponses.isEmpty()){
            return cachedCommentResponses;
        }

        List<CommentResponse> commentResponses = commentRepository.findByAccountId(accountId).stream()
                .map(CommentMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allComment",commentResponses,600);

        return commentResponses;
    }

    // Lấy Comment theo ID
    @Transactional
    @Override
    public CommentResponse getCommentById(UUID id) {
        CommentResponse cachedCommentResponse = redisService.getObject("comment:"+id, new TypeReference<CommentResponse>() {});
        if(cachedCommentResponse != null){
            return cachedCommentResponse;
        }

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        CommentResponse commentResponse = CommentMapper.convertToResponse(comment);

        redisService.setObject("comment:"+id,commentResponse,600);

        return commentResponse;
    }

    // 3. Tạo một Comment mới
    @Override
    @Transactional
    public CommentResponse createComment(CommentDTO commentDTO) {
        if(commentDTO.getBody() == null){
            throw  new IllegalArgumentException("body cannot be null");
        }

        Account account = accountRepository.findById(commentDTO.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        LaptopModel laptopModel = laptopModelRepository.findById(commentDTO.getLaptopModelId())
                .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found"));

        Comment comment = Comment.builder()
                .replies(null)
                .id(null)
                .account(account)
                .body(commentDTO.getBody())
                .laptopModel(laptopModel)
                .build();

        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent Comment not found"));
            comment.setParent(parentComment);
//            parentComment.getReplies().add(comment);
        }

        Comment commentExisting = commentRepository.save(comment);

        CommentResponse cachedCommentResponse = CommentMapper.convertToResponse(commentExisting);

        redisService.deleteByPatterns(List.of("allComment","allLaptopModel","laptopModel:"+commentDTO.getLaptopModelId()));
        redisService.setObject("comment:"+cachedCommentResponse.getId(),cachedCommentResponse,600);

        return cachedCommentResponse;
    }

    // 4. Cập nhật một Comment
    @Override
    @Transactional
    public CommentResponse updateComment(UUID commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        comment.setBody(commentDTO.getBody());

        Comment commentExisting = commentRepository.save(comment);

        CommentResponse cachedCommentResponse = CommentMapper.convertToResponse(commentExisting);

        redisService.deleteByPatterns(List.of("allComment","allLaptopModel","laptopModel:"+commentDTO.getLaptopModelId(),"comment:"+commentId));
        redisService.setObject("comment:"+cachedCommentResponse.getId(),cachedCommentResponse,600);

        return cachedCommentResponse;
    }

    @Override
    public CommentResponse partialUpdateComment(UUID id, Map<String, Object> fieldsToUpdate) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with ID " + id + " not found!"));

        Class<?> clazz = comment.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    field.set(comment, newValue);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Comment updatedComment = commentRepository.save(comment);
        CommentResponse cachedCommentResponse = CommentMapper.convertToResponse(updatedComment);

        redisService.deleteByPatterns(List.of("allComment","allLaptopModel","laptopModel:"+cachedCommentResponse.getLaptopModel().getId(),"comment:"+id));
        redisService.setObject("comment:"+cachedCommentResponse.getId(),cachedCommentResponse,600);

        return cachedCommentResponse;
    }

    // 5. Xóa Comment theo ID
    @Override
    @Transactional
    public void deleteComment(UUID id) {
        Comment commentExisting = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        redisService.deleteByPatterns(List.of("allComment","allLaptopModel","laptopModel:"+commentExisting.getLaptopModel().getId(),"comment:"+id));

        commentRepository.delete(commentExisting);
    }


}