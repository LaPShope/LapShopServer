package com.example.demo.service;

import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.dto.response.OrderDetailResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderDetailService {
    OrderDetailResponse partialUpdateOrderDetail(UUID id, Map<String,Object> fieldsToUpdate);
    List<OrderDetailResponse> getAllOrderDetails();                     // Lấy danh sách tất cả OrderDetail
    OrderDetailResponse getOrderDetailById(UUID id);                    // Lấy OrderDetail theo ID
    OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO);         // Tạo OrderDetail mới
    OrderDetailResponse updateOrderDetail(UUID id, OrderDetailDTO orderDetailDTO);// Cập nhật OrderDetail
    void deleteOrderDetail(UUID id);                               // Xóa OrderDetail
}