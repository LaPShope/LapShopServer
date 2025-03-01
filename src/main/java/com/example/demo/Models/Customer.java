package com.example.demo.Models;


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
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @MapsId
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "id")
    private Account customerId;

//    @Column(nullable = false)
    private String gender;

    @Column(name = "born_date")
    private Date bornDate;

//    @Column(nullable = false)
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
