package com.example.demo.Repository;

import com.example.demo.Common.Enums;
import com.example.demo.Models.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, UUID> {
    @Query("SELECT l FROM Laptop l JOIN l.laptopModel lm WHERE " +
            "(?1 IS NULL OR LOWER(lm.name) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR LOWER(lm.branch) LIKE LOWER(CONCAT('%', ?2, '%'))) " +
            "AND (?3 IS NULL OR LOWER(lm.cpu) LIKE LOWER(CONCAT('%', ?3, '%'))) " +
            "AND (?4 IS NULL OR lm.ram = ?4) " +
            "AND (?5 IS NULL OR l.status = ?5) " +
            "AND (?6 IS NULL OR LOWER(lm.description) LIKE LOWER(CONCAT('%', ?6, '%'))) " +
            "AND (?7 IS NULL OR LOWER(lm.display) LIKE LOWER(CONCAT('%', ?7, '%'))) " +
            "AND (?8 IS NULL OR LOWER(lm.storage) LIKE LOWER(CONCAT('%', ?8, '%'))) " +
            "AND (?9 IS NULL OR lm.color = ?9) " +
            "AND (?10 IS NULL OR lm.price >= ?10) " +
            "AND (?11 IS NULL OR lm.price <= ?11)")
    List<Laptop> searchLaptops(
            String name,
            String branch,
            String cpu,
            String ram,
            Enums.laptopStatus status,
            String description,
            String display,
            String storage,
            Enums.Color color,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );


}