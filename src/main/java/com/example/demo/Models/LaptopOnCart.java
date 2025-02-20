package com.example.demo.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="laptop_on_cart")
public class LaptopOnCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(cascade = { CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "cart_id",nullable = false)
    private Cart cart;

    @ManyToOne(cascade = { CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "laptop_on_cart",nullable = false)
    private LaptopModel laptopModel;

    @Column(nullable = false)
    private Integer quantity;

}
