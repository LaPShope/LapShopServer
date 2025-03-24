package com.example.demo.repository;

import com.example.demo.model.LaptopOnCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LaptopOnCartRepository extends JpaRepository<LaptopOnCart, UUID> {
}
