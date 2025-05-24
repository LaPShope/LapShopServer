package com.example.demo.repository;

import com.example.demo.model.LaptopOnCart;
import com.example.demo.model.LaptopOnCartKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LaptopOnCartRepository extends JpaRepository<LaptopOnCart, LaptopOnCartKey> {
    /**
     * Finds a LaptopOnCart by cart ID and laptop model ID.
     *
     * @param cartId        the ID of the cart
     * @param laptopModelId the ID of the laptop model
     * @return an Optional containing the LaptopOnCart if found, or empty if not found
     */
    Optional<LaptopOnCart> findByCartIdAndLaptopModelId(UUID cartId, UUID laptopModelId);

}
