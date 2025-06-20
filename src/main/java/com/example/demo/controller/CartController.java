package com.example.demo.controller;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.request.cart.AddLaptopToCart;
import com.example.demo.dto.request.cart.CartDTO;
import com.example.demo.dto.response.cart.CartResponse;
import com.example.demo.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts") // Base URL cho Cart
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 1. API: Lấy tất cả Cart
    @GetMapping
    public ResponseEntity<DataResponse<CartResponse>> getAllCarts() {
        return ResponseEntity.ok(DataResponse.<CartResponse>builder()
                .success(true)
                .message("Cart retrieved successfully")
                .data(cartService.getAllCartsOnCustomer())
                .build());
    }

    // Lấy Cart theo ID
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<CartResponse>> getCartById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(DataResponse.<CartResponse>builder()
                .success(true)
                .message("Addresses retrieved successfully")
                .data(cartService.getCartById(id))
                .build());
    }

    //  API: Tạo mới Cart
    @PostMapping
    public ResponseEntity<?> createCart(@RequestBody CartDTO cartDTO) {
        return ResponseEntity.ok(DataResponse.<CartResponse>builder()
                .success(true)
                .message("Addresses created successfully")
                .data(cartService.createCart(cartDTO))
                .build());
    }

    @PostMapping("/add-laptop-to-cart")
    public ResponseEntity<?> addLaptopToCart(@RequestBody AddLaptopToCart requestBody) {
        return ResponseEntity.ok(DataResponse.<CartResponse>builder()
                .success(true)
                .message("Laptop added to cart successfully")
                .data(cartService.addLaptopToCart(requestBody))
                .build());
    }

    //  Cập nhật Cart theo ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCart(@PathVariable("id") UUID id, @RequestBody CartDTO cartDTO) {
        return ResponseEntity.ok(DataResponse.<CartResponse>builder()
                .success(true)
                .message("Addresses updated successfully")
                .data(cartService.updateCart(id, cartDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataResponse<CartResponse>> partialUpdateCart(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fieldsToUpdate) {

        return ResponseEntity.ok(DataResponse.<CartResponse>builder()
                .success(true)
                .message("Cart updated successfully!")
                .data(cartService.partialUpdateCart(id, fieldsToUpdate))
                .build());
    }


    // 5. API: Xóa Cart theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable("id") UUID id) {
        cartService.deleteCart(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Addresses deleted successfully")
                .build());
    }
}