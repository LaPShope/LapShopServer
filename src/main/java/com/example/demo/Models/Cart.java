package com.example.demo.Models;


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
@Table(name="cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer quantity;

    @JsonIgnore
    @OneToMany(mappedBy = "cart",cascade = {CascadeType.PERSIST,
                                            CascadeType.DETACH,
                                            CascadeType.MERGE,
                                            CascadeType.REFRESH},orphanRemoval = true)
    private List<LaptopOnCart> laptopOnCarts;
}
