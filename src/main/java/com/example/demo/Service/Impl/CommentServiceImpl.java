package com.example.demo.Service.Impl;

import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.Response.CommentResponse.CommentResponse;
import com.example.demo.Models.Comment;
import com.example.demo.Models.Account;
import com.example.demo.Models.LaptopModel;
import com.example.demo.Repository.CommentRepository;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.LaptopModelRepository;
import com.example.demo.Service.CommentService;
import com.example.demo.mapper.CommentMapper;
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

    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final LaptopModelRepository laptopModelRepository;

    public CommentServiceImpl(CommentRepository commentRepository, AccountRepository accountRepository, LaptopModelRepository laptopModelRepository) {
        this.commentRepository = commentRepository;
        this.accountRepository = accountRepository;
        this.laptopModelRepository = laptopModelRepository;
    }

    // Lấy danh sách tất cả Comment
    @Transactional
    @Override
    public List<CommentResponse> getAllCommentsByAccountId(UUID accountId) {
        return commentRepository.findByAccountId(accountId).stream()
                .map(CommentMapper::convertToResponse)
                .collect(Collectors.toList());
    }

    // Lấy Comment theo ID
    @Transactional
    @Override
    public CommentResponse getCommentById(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        return CommentMapper.convertToResponse(comment);
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

        return CommentMapper.convertToResponse(commentExisting);
    }

    // 4. Cập nhật một Comment
    @Override
    @Transactional
    public CommentResponse updateComment(UUID commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        comment.setBody(commentDTO.getBody());

        // Cập nhật Account
        if (commentDTO.getAccountId() != null) {
            Account account = accountRepository.findById(commentDTO.getAccountId())
                    .orElseThrow(() -> new EntityNotFoundException("Account not found"));
            comment.setAccount(account);
        }

        // Cập nhật LaptopModel nếu có
        if (commentDTO.getLaptopModelId() != null) {
            LaptopModel laptopModel = laptopModelRepository.findById(commentDTO.getLaptopModelId())
                    .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found "));
            comment.setLaptopModel(laptopModel);
        }

        // Cập nhật Parent Comment nếu có
        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent Comment not found"));
            comment.setParent(parentComment);
        }

        if (commentDTO.getReplies() != null) {
            List<Comment> replies = commentDTO.getReplies().stream()
                    .map(replyId -> commentRepository.findById(replyId)
                            .orElseThrow(() -> new EntityNotFoundException("Reply Comment not found")))
                    .collect(Collectors.toList());

            comment.setReplies(replies);
        }

        Comment commentExisting = commentRepository.save(comment);

        return CommentMapper.convertToResponse(commentExisting);
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
        return CommentMapper.convertToResponse(updatedComment);
    }

    // 5. Xóa Comment theo ID
    @Override
    @Transactional
    public void deleteComment(UUID id) {
        Comment commentExisting = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        commentRepository.delete(commentExisting);
    }


}