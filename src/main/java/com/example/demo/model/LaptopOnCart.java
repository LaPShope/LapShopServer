package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "laptop_on_cart")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LaptopOnCart {

    @EmbeddedId
    private LaptopOnCartKey id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @MapsId("cartId") // maps to id.cartId
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @MapsId("laptopModelId") // maps to id.laptopModelId
    @JoinColumn(name = "laptop_model_id", nullable = false)
    private LaptopModel laptopModel;

    @Column(nullable = false)
    private Integer quantity;
}