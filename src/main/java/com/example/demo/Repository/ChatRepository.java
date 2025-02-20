package com.example.demo.Repository;

import com.example.demo.Models.Account;
import com.example.demo.Models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findBySenderId(Account sender);
    List<Chat> findByReceiverId(Account receiver);
}