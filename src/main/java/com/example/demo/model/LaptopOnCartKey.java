package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LaptopOnCartKey implements Serializable {
    @Column(name = "cart_id", columnDefinition = "BINARY(16)")
    private UUID cartId;

    @Column(name = "laptop_model_id", columnDefinition = "BINARY(16)")
    private UUID laptopModelId;
}
