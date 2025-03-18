package com.example.demo.Repository;

import com.example.demo.Models.Account;
import com.example.demo.Models.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
}
