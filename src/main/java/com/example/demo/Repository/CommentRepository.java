package com.example.demo.Repository;

import com.example.demo.Models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    // Tìm tất cả comment của tài khoản (Account)
    List<Comment> findByAccountId(UUID accountId);

    // Tìm tất cả replies (comment con) của theo parentId
    List<Comment> findByParentId(UUID parentId);

    // Tìm tất cả comment theo LaptopModel
    List<Comment> findByLaptopModelId(UUID laptopModelId);
}