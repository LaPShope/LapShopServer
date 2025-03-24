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

    @MapsId
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "id")
    private Account customerId;

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
    private List<Order> oderList;

    @JsonIgnore
    @OneToMany(mappedBy = "customer",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    private List<Cart> cartList;

    @Override
    public String toString() {
        return "Customer{id=" + id + "}";
    }

}
