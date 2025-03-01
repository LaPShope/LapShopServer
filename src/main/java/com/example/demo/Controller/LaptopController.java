package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.LaptopDTO;
import com.example.demo.DTO.Response.LaptopResponse;
import com.example.demo.Service.LaptopService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laptops")
public class LaptopController {

    private final LaptopService laptopService;

    public LaptopController(LaptopService laptopService) {
        this.laptopService = laptopService;
    }

    // 1. Lấy tất cả Laptop
    @GetMapping
    public ResponseEntity<DataResponse<List<LaptopDTO>>> getAllLaptops() {
        return ResponseEntity.ok(DataResponse.<List<LaptopDTO>>builder()
                .success(true)
                .message("Laptop retrieved successfully")
                .data(laptopService.getAllLaptops())
                .build());
    }

    // 2. Lấy Laptop chi tiết theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getLaptopById(@PathVariable UUID id) {
        return ResponseEntity.ok(DataResponse.<LaptopDTO>builder()
                .success(true)
                .message("Laptop retrieved successfully")
                .data(laptopService.getLaptopById(id))
                .build());
    }

    // 3. Tạo mới Laptop
    @PostMapping
    public ResponseEntity<?> createLaptop(@RequestBody LaptopDTO laptopDTO) {
            ;
        return ResponseEntity.ok(DataResponse.<LaptopDTO>builder()
                .success(true)
                .message("Laptop created successfully")
                .data(laptopService.createLaptop(laptopDTO))
                .build());
    }

    // 4. Cập nhật Laptop
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLaptop(@PathVariable UUID id, @RequestBody LaptopDTO laptopDTO) {

        return ResponseEntity.ok(DataResponse.<LaptopDTO>builder()
                .success(true)
                .message("Laptop updated successfully")
                .data(laptopService.updateLaptop(id,laptopDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateLaptop(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<LaptopDTO>builder()
                .success(true)
                .message("Laptop updated successfully")
                .data(laptopService.partialUpdateLaptop(id, fieldsToUpdate))
                .build());
    }

    // 5. Xóa Laptop
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLaptop(@PathVariable UUID id) {
            laptopService.deleteLaptop(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Laptop deleted successfully")
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<DataResponse<List<LaptopResponse>>> searchLaptops(@RequestParam Map<String, Object> filters) {
        return ResponseEntity.ok(DataResponse.<List<LaptopResponse>>builder()
                .success(true)
                .message("Laptops found")
                .data(laptopService.searchLaptops(filters))
                .build());
    }

}