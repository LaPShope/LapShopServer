package com.example.demo.Models;


import jakarta.persistence.*;
import lombok.*;


import com.example.demo.Common.Enums;

import java.sql.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="laptop")
public class Laptop {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)",name = "mac_id")
    private UUID macId;

    @Column(nullable = false)
    private Date MFG;

    @ManyToOne(cascade = { CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "model_id")
    private LaptopModel laptopModel;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Enums.laptopStatus status;


}
