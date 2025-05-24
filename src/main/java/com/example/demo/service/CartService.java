package com.example.demo.service;

import com.example.demo.dto.request.cart.AddLaptopToCart;
import com.example.demo.dto.request.cart.CartDTO;
import com.example.demo.dto.response.cart.CartResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {

    CartResponse partialUpdateCart(UUID id, Map<String, Object> fieldsToUpdate);

    List<CartResponse> getAllCartsOnCustomer(); // Lấy tất cả Cart

    CartResponse getCartById(UUID id); // Lấy Cart theo ID

    CartResponse createCart(CartDTO cartDTO); // Tạo mới Cart

    CartResponse updateCart(UUID id, CartDTO cartDTO); // Cập nhật Cart theo ID

    void deleteCart(UUID id); // Xóa Cart theo ID

    CartResponse addLaptopToCart(AddLaptopToCart requestBody); // Thêm Laptop vào Cart
}