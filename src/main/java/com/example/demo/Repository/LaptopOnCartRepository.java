package com.example.demo.Repository;

import com.example.demo.Models.LaptopOnCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LaptopOnCartRepository extends JpaRepository<LaptopOnCart, UUID> {
}
