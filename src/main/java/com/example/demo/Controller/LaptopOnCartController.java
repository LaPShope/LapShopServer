package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.LaptopDTO;
import com.example.demo.DTO.LaptopOnCartDTO;
import com.example.demo.DTO.Response.LaptopOnCartResponse;
import com.example.demo.DTO.SaleDTO;
import com.example.demo.Service.LaptopOnCartService;
import org.springframework.http.HttpStatus;
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
                .data( laptopOnCartService.getAllLaptopOnCarts())
                .build());
    }

    // 2. Lấy LaptopOnCart theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getLaptopOnCartById(@PathVariable UUID id) {

        return ResponseEntity.ok(DataResponse.<LaptopOnCartResponse>builder()
                .success(true)
                .message("Sale retrieved successfully")
                .data(laptopOnCartService.getLaptopOnCartById(id))
                .build());

    }

    // 3. Tạo mới LaptopOnCart
    @PostMapping
    public ResponseEntity<?> createLaptopOnCart(@RequestBody LaptopOnCartDTO laptopOnCartDTO) {

        return ResponseEntity.ok(DataResponse.<LaptopOnCartResponse>builder()
                .success(true)
                .message("Sale created successfully")
                .data(laptopOnCartService.createLaptopOnCart(laptopOnCartDTO))
                .build());

    }

    // 4. Cập nhật LaptopOnCart
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLaptopOnCart(@PathVariable UUID id, @RequestBody LaptopOnCartDTO laptopOnCartDTO) {

        return ResponseEntity.ok(DataResponse.<LaptopOnCartResponse>builder()
                .success(true)
                .message("Sale updated successfully")
                .data(laptopOnCartService.updateLaptopOnCart(id, laptopOnCartDTO))
                .build());

    }

    // 5. Xóa LaptopOnCart
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLaptopOnCart(@PathVariable UUID id) {

            laptopOnCartService.deleteLaptopOnCart(id);
        return ResponseEntity.ok(DataResponse.<LaptopOnCartDTO>builder()
                .success(true)
                .message("Sale retrieved successfully")
                .build());

    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateLaptopOnCart(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<LaptopOnCartResponse>builder()
                .success(true)
                .message("Laptop updated successfully")
                .data(laptopOnCartService.partialUpdateLaptopOnCart(id, fieldsToUpdate))
                .build());
    }
}