package com.example.demo.Service;

import com.example.demo.DTO.CartDTO;
import com.example.demo.DTO.Response.CartResponse.CartResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {

    CartResponse partialUpdateCart(UUID id, Map<String,Object> fieldsToUpdate);

    List<CartResponse> getAllCarts(); // Lấy tất cả Cart

    CartResponse getCartById(UUID id); // Lấy Cart theo ID

    CartResponse createCart(CartDTO cartDTO); // Tạo mới Cart

    CartResponse updateCart(UUID id, CartDTO cartDTO); // Cập nhật Cart theo ID

    void deleteCart(UUID id); // Xóa Cart theo ID
}