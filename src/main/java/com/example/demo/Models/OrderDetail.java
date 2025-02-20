package com.example.demo.Models;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="order_detail")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(cascade = { CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "oder_id")
    private Order order;

    @ManyToOne(cascade = { CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "laptop_model_id",nullable = false)
    private LaptopModel laptopModel;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;


}
