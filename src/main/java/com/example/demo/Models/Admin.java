package com.example.demo.Models;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="admin")
public class Admin {
    @Id
    private UUID id;

    @MapsId
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "account_id")
    private Account adminId;
}