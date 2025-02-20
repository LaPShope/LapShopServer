package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.ImageDTO;
import com.example.demo.DTO.OrderDetailDTO;
import com.example.demo.Service.OrderDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order-details")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    // Get all order details
    @GetMapping
    public ResponseEntity<DataResponse<List<OrderDetailDTO>>> getAllOrderDetails() {

        return ResponseEntity.ok(DataResponse.<List<OrderDetailDTO>>builder()
                .success(true)
                .message("OrderDetail retrieved successfully")
                .data(orderDetailService.getAllOrderDetails())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<OrderDetailDTO>> getOrderDetailById(@PathVariable UUID id) {

        return ResponseEntity.ok(DataResponse.<OrderDetailDTO>builder()
                .success(true)
                .message("OrderDetail retrieved successfully")
                .data(orderDetailService.getOrderDetailById(id))
                .build());
    }

    // Create new order detail
    @PostMapping
    public ResponseEntity<?> createOrderDetail(@RequestBody OrderDetailDTO orderDetailDTO) {

        return ResponseEntity.ok(DataResponse.<OrderDetailDTO>builder()
                .success(true)
                .message("OrderDetail created successfully")
                .data(orderDetailService.createOrderDetail(orderDetailDTO))
                .build());
    }

    // Update order detail by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@PathVariable UUID id, @RequestBody OrderDetailDTO orderDetailDTO) {

        return ResponseEntity.ok(DataResponse.<OrderDetailDTO>builder()
                .success(true)
                .message("OrderDetail updated successfully")
                .data(orderDetailService.updateOrderDetail(id, orderDetailDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateOrderDetail(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<OrderDetailDTO>builder()
                .success(true)
                .message("OrderDetail updated successfully")
                .data(orderDetailService.partialUpdateOrderDetail(id, fieldsToUpdate))
                .build());
    }

    // Delete order detail by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable UUID id) {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("OrderDetail deleted successfully")
                .build());
    }
}