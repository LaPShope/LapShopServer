package com.example.demo.Service;

import com.example.demo.DTO.OrderDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {
    OrderDTO partialUpdateOrder(UUID id, Map<String,Object> fieldsToUpdate);

    List<OrderDTO> getAllOrders(); // Lấy danh sách tất cả Orders

    OrderDTO getOrderById(UUID id); // Lấy Order theo ID

    OrderDTO createOrder(OrderDTO orderDTO); // Tạo Order mới

    OrderDTO updateOrder(UUID id, OrderDTO orderDTO); // Cập nhật Order

    void deleteOrder(UUID id); // Xóa Order theo ID
}