package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.Response.OrderResponse.OrderResponse;
import com.example.demo.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {

        return ResponseEntity.ok(DataResponse.<List<OrderResponse>>builder()
                .success(true)
                .message("Orders retrieved successfully")
                .data(orderService.getAllOrders())
                .build());
    }

    // Fetch Order theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable UUID id) {

        return ResponseEntity.ok(DataResponse.<OrderResponse>builder()
                .success(true)
                .message("Order retrieved successfully")
                .data(orderService.getOrderById(id))
                .build());
    }

    // Tạo mới một Order
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
            ;
        return ResponseEntity.ok(DataResponse.<OrderResponse>builder()
                .success(true)
                .message("Order created successfully")
                .data(orderService.createOrder(orderDTO))
                .build());
    }

    // Cập nhật Order theo ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable UUID id, @RequestBody OrderDTO orderDTO) {

        return ResponseEntity.ok(DataResponse.<OrderResponse>builder()
                .success(true)
                .message("Order updated successfully")
                .data(orderService.updateOrder(id, orderDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateOrder(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<OrderResponse>builder()
                .success(true)
                .message("Order updated successfully")
                .data(orderService.partialUpdateOrder(id, fieldsToUpdate))
                .build());
    }


    // Xóa Order theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID id) {
            orderService.deleteOrder(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Order created successfully")
                .build());
    }
}