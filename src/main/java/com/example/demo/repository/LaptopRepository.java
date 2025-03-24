package com.example.demo.repository;

import com.example.demo.model.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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