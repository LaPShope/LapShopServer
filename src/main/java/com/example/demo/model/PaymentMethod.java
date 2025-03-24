package com.example.demo.model;

import com.example.demo.common.Enums;
import com.example.demo.common.JsonConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="payment_method")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Lob
    @Column(columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    private Map<String,Object> data;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Enums.PaymentType type;

    @JsonIgnore
    @OneToMany(mappedBy = "paymentMethod",cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REMOVE,
            CascadeType.REFRESH})
    private List<Payment> paymentList;
}
