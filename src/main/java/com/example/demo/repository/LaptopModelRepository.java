package com.example.demo.repository;

import com.example.demo.model.LaptopModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LaptopModelRepository extends JpaRepository<LaptopModel, UUID> {
    public Optional<LaptopModel> findByName(String modelName);
}
