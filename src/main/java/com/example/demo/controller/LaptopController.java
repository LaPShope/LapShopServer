package com.example.demo.controller;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.LaptopDTO;
import com.example.demo.dto.response.LaptopResponse;
import com.example.demo.service.LaptopService;

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
    public ResponseEntity<DataResponse<List<LaptopResponse>>> getAllLaptops() {
        return ResponseEntity.ok(DataResponse.<List<LaptopResponse>>builder()
                .success(true)
                .message("Laptop retrieved successfully")
                .data(laptopService.getAllLaptops())
                .build());
    }

    // 2. Lấy Laptop chi tiết theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getLaptopById(@PathVariable UUID id) {
        return ResponseEntity.ok(DataResponse.<LaptopResponse>builder()
                .success(true)
                .message("Laptop retrieved successfully")
                .data(laptopService.getLaptopById(id))
                .build());
    }

    // 3. Tạo mới Laptop
    @PostMapping
    public ResponseEntity<?> createLaptop(@RequestBody LaptopDTO laptopDTO) {
            ;
        return ResponseEntity.ok(DataResponse.<LaptopResponse>builder()
                .success(true)
                .message("Laptop created successfully")
                .data(laptopService.createLaptop(laptopDTO))
                .build());
    }

    // 4. Cập nhật Laptop
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLaptop(@PathVariable UUID id, @RequestBody LaptopDTO laptopDTO) {

        return ResponseEntity.ok(DataResponse.<LaptopResponse>builder()
                .success(true)
                .message("Laptop updated successfully")
                .data(laptopService.updateLaptop(id,laptopDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateLaptop(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<LaptopResponse>builder()
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

    @GetMapping("/search/on-sale")
    public ResponseEntity<DataResponse<List<LaptopResponse>>> searchLaptopsOnSale() {
        return ResponseEntity.ok(DataResponse.<List<LaptopResponse>>builder()
                .success(true)
                .message("Laptops found")
                .data(laptopService.searchLaptopsOnSale())
                .build());
    }

    // 8. Lấy Laptop với phân trang
    @GetMapping("/pagination")
    public ResponseEntity<DataResponse<?>> getLaptopsWithPagination(
            @RequestParam int offset,
            @RequestParam int pageSize) {
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Laptops retrieved successfully")
                .data(laptopService.getLaptopsWithPagination(offset, pageSize))
                .build());
    }

    // 9. Lấy Laptop với phân trang và sắp xếp theo giá tăng dần
    @GetMapping("/pagination/sort/price-asc")
    public ResponseEntity<DataResponse<?>> getLaptopsWithPaginationAndSortByPriceASC(
            @RequestParam double price,
            @RequestParam int offset,
            @RequestParam int pageSize) {
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Laptops retrieved and sorted by price in ascending order")
                .data(laptopService.getLaptopsWithPaginationAndSortByPriceASC(price, offset, pageSize))
                .build());
    }

    // 10. Lấy Laptop với phân trang và sắp xếp theo giá giảm dần
    @GetMapping("/pagination/sort/price-des")
    public ResponseEntity<DataResponse<?>> getLaptopsWithPaginationAndSortByPriceDES(
            @RequestParam double price,
            @RequestParam int offset,
            @RequestParam int pageSize) {
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Laptops retrieved and sorted by price in descending order")
                .data(laptopService.getLaptopsWithPaginationAndSortByPriceDES(price, offset, pageSize))
                .build());
    }

    // 11. Lấy Laptop với phân trang và theo Brand
    @GetMapping("/pagination/brand")
    public ResponseEntity<DataResponse<?>> getLaptopsWithPaginationByBrand(
            @RequestParam int offset,
            @RequestParam int pageSize,
            @RequestParam String brand) {
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Laptops retrieved successfully with pagination and brand filter")
                .data(laptopService.getLaptopsWithPaginationByBrand(offset, pageSize, brand))
                .build());
    }

}