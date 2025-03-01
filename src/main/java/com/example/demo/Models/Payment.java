package com.example.demo.Models;

import jakarta.persistence.*;
import lombok.*;


import com.example.demo.Common.Enums;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;

    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "payment_method_id",nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Enums.PaymentType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Enums.PaymentStatus status;


}

