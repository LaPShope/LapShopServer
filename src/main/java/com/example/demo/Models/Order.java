package com.example.demo.Models;

import com.example.demo.Common.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Enums.OrderStatus status = Enums.OrderStatus.Pending;

    @Column(name = "date_create", nullable = false)
    private Date dateCreate;

    @JsonIgnore
    @OneToMany(mappedBy = "order",cascade = {CascadeType.PERSIST,
                                            CascadeType.DETACH,
                                            CascadeType.REMOVE,
                                            CascadeType.MERGE,
                                            CascadeType.REFRESH})
    private List<OrderDetail> orderDetailList;

    @JsonIgnore
    @OneToOne(mappedBy = "order",cascade = {CascadeType.PERSIST,
                                            CascadeType.DETACH,
                                            CascadeType.REMOVE,
                                            CascadeType.MERGE,
                                            CascadeType.REFRESH})
    private Payment payment;

}
