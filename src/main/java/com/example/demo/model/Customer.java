package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="customer")
public class Customer {
    @Id
    private UUID id;

    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @MapsId
    private Account account;

    private String gender;

    @Column(name = "born_date")
    private Date bornDate;

    private String phone;

    private String avatar;

    @JsonIgnore
    @OneToMany(mappedBy = "customer",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    private List<Address> addressList;

    @JsonIgnore
    @OneToMany(mappedBy = "customer",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    private List<Payment> paymentList;

    @JsonIgnore
    @OneToMany(mappedBy = "customer",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    private List<Order> orderList;

    @JsonIgnore
    @OneToMany(mappedBy = "customer",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH},orphanRemoval = true)
    private List<Cart> cartList;

}
