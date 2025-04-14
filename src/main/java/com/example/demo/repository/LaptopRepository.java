package com.example.demo.repository;

import com.example.demo.model.Laptop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT l FROM Laptop l JOIN l.laptopModel lm WHERE lm.price = ?1 ORDER BY lm.price ASC")
    Page<Laptop> findLaptopsByPriceSortedASC(double price, Pageable pageable);

    @Query("SELECT l FROM Laptop l JOIN l.laptopModel lm WHERE lm.price = ?1 ORDER BY lm.price DESC")
    Page<Laptop> findLaptopsByPriceSortedDES(double price, Pageable pageable);

}