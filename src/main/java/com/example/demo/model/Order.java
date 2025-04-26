package com.example.demo.model;

import com.example.demo.common.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="`order`")
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

    private String address;

    @Column(name="delivery_cost")
    private BigDecimal deliveryCost;
    
    @Column(name="final_price")
    private BigDecimal finalPrice;

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
                                            CascadeType.REFRESH},orphanRemoval = true)
    private Payment payment;

}
