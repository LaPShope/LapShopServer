package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    // make it unique
    @JoinColumn(name = "customer_id", unique = true, nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Customer customer;

    @JsonIgnore
    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.REMOVE,
            CascadeType.MERGE,
            CascadeType.REFRESH}, orphanRemoval = true)
    private List<LaptopOnCart> laptopOnCarts;
}
