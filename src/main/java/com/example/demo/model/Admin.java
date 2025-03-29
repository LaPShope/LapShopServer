package com.example.demo.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "admin")
public class Admin {
    @Id
    private UUID id;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @MapsId
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
}