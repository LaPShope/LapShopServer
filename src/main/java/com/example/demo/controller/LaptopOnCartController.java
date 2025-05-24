package com.example.demo.controller;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.LaptopOnCartDTO;
import com.example.demo.dto.response.LaptopOnCartResponse;
import com.example.demo.service.LaptopOnCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laptop-on-carts")
public class LaptopOnCartController {

    private final LaptopOnCartService laptopOnCartService;

    public LaptopOnCartController(LaptopOnCartService laptopOnCartService) {
        this.laptopOnCartService = laptopOnCartService;
    }

    // 1. Lấy tất cả LaptopOnCart
    @GetMapping
    public ResponseEntity<?> getAllLaptopOnCarts() {
        return ResponseEntity.ok(DataResponse.<List<LaptopOnCartResponse>>builder()
                .success(true)
                .message("Sale retrieved successfully")
                .data(laptopOnCartService.getAllLaptopOnCarts())
                .build());
    }

    // 2. Lấy LaptopOnCart theo ID
    @GetMapping("/detail")
    public ResponseEntity<Object> getLaptopOnCartByCartIdAndLaptopModelId(
            @RequestParam(
                    value = "cart-id",
                    required = true
            ) UUID cartId,
            @RequestParam(
                    value = "laptop-model-id",
                    required = true
            ) UUID laptopModelId) {

        return ResponseEntity.ok(DataResponse.<LaptopOnCartResponse>builder()
                .success(true)
                .message("Sale retrieved successfully")
                .data(laptopOnCartService.getLaptopOnCartByCartIdAndLaptopModelId(cartId, laptopModelId))
                .build());
    }

    // 3. Tạo mới LaptopOnCart
    @PostMapping
    public ResponseEntity<?> createLaptopOnCart(@RequestBody LaptopOnCartDTO laptopOnCartDTO) {
        // System.out.println("LaptopOnCartDTO: " + laptopOnCartDTO);
        return ResponseEntity.ok(DataResponse.<LaptopOnCartResponse>builder()
                .success(true)
                .message("Sale created successfully")
                .data(laptopOnCartService.createLaptopOnCart(laptopOnCartDTO))
                .build());

    }

    // 4. Cập nhật LaptopOnCart
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateLaptopOnCart(@PathVariable UUID id, @RequestBody LaptopOnCartDTO laptopOnCartDTO) {
//
//        return ResponseEntity.ok(DataResponse.<LaptopOnCartResponse>builder()
//                .success(true)
//                .message("Sale updated successfully")
//                .data(laptopOnCartService.updateLaptopOnCart(id, laptopOnCartDTO))
//                .build());
//
//    }

    // 5. Xóa LaptopOnCart
    @DeleteMapping()
    public ResponseEntity<?> deleteLaptopOnCart(
            @RequestParam(
                    value = "cart-id",
                    required = true
            ) UUID cartId,
            @RequestParam(
                    value = "laptop-model-id",
                    required = true
            ) UUID laptopModelId) {
        laptopOnCartService.deleteLaptopOnCart(cartId, laptopModelId);
        return ResponseEntity.ok(DataResponse.<LaptopOnCartDTO>builder()
                .success(true)
                .message("Sale retrieved successfully")
                .build());
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<?> partialUpdateLaptopOnCart(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
//        return ResponseEntity.ok(DataResponse.<LaptopOnCartResponse>builder()
//                .success(true)
//                .message("Laptop updated successfully")
//                .data(laptopOnCartService.partialUpdateLaptopOnCart(id, fieldsToUpdate))
//                .build());
//    }
}