package com.example.demo.Service;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.OrderDetailDTO;
import com.example.demo.DTO.Response.OrderDetailResponse;

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