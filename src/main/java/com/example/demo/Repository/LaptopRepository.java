package com.example.demo.Repository;

import com.example.demo.Common.Enums;
import com.example.demo.Models.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, UUID> {
    @Query("""
    SELECT l FROM Laptop l 
    JOIN l.laptopModel lm 
    JOIN lm.saleList s 
    WHERE s.startAt <= ?1 AND s.endAt >= ?1
""")
    List<Laptop> findLaptopsOnSale(Date now);

}