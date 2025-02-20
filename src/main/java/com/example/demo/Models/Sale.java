package com.example.demo.Models;

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
@Table(name="sale")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String event_description;

    @Column(name = "start_at", nullable = false)
    private Date startAt;

    @Column(name = "end_at", nullable = false)
    private Date endAt;

    @Column(nullable = false)
    private Float discount;

    @ManyToMany(cascade = {CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @JoinTable(
            name = "laptop_on_sale",
            joinColumns = @JoinColumn(name = "sale_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "laptop_model_id")
    )
    private List<LaptopModel> laptopModelList;

}
