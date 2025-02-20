package com.example.demo.Service;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.OrderDetailDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderDetailService {
    OrderDetailDTO partialUpdateOrderDetail(UUID id, Map<String,Object> fieldsToUpdate);
    List<OrderDetailDTO> getAllOrderDetails();                     // Lấy danh sách tất cả OrderDetail
    OrderDetailDTO getOrderDetailById(UUID id);                    // Lấy OrderDetail theo ID
    OrderDetailDTO createOrderDetail(OrderDetailDTO orderDetailDTO);         // Tạo OrderDetail mới
    OrderDetailDTO updateOrderDetail(UUID id, OrderDetailDTO orderDetailDTO);// Cập nhật OrderDetail
    void deleteOrderDetail(UUID id);                               // Xóa OrderDetail
}