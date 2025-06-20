package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.response.order.OrderResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {

    List<OrderResponse> getAllOrdersByCustomer(); // Lấy danh sách Orders theo Customer

    OrderResponse partialUpdateOrder(UUID id, Map<String,Object> fieldsToUpdate);

    List<OrderResponse> getAllOrders(); // Lấy danh sách tất cả Orders

    OrderResponse getOrderById(UUID id); // Lấy Order theo ID

    OrderResponse createOrder(OrderDTO orderDTO); // Tạo Order mới

    OrderResponse updateOrder(UUID id, OrderDTO orderDTO); // Cập nhật Order

    void deleteOrder(UUID id); // Xóa Order theo ID
}