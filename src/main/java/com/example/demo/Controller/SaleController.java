package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.AddressDTO;
import com.example.demo.DTO.Response.SaleResponse;
import com.example.demo.DTO.SaleDTO;
import com.example.demo.Service.SaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales") // Base URL cho Sale
public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    // Lấy danh sách tất cả Sale
    @GetMapping
    public ResponseEntity<?> getAllSales() {

        return ResponseEntity.ok(DataResponse.<List<SaleResponse>>builder()
                .success(true)
                .message("Sale retrieved successfully")
                .data(saleService.getAllSales())
                .build());
    }

    // Lấy Sale theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSaleById(@PathVariable UUID id) {

        return ResponseEntity.ok(DataResponse.<SaleResponse>builder()
                .success(true)
                .message("Sale retrieved successfully")
                .data(saleService.getSaleById(id))
                .build());
    }

    // Tạo mới một Sale
    @PostMapping
    public ResponseEntity<?> createSale(@RequestBody SaleDTO saleDTO) {

        return ResponseEntity.ok(DataResponse.<SaleResponse>builder()
                .success(true)
                .message("Sale created successfully")
                .data(saleService.createSale(saleDTO))
                .build());
    }


    // Xóa Sale theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable UUID id) {
            saleService.deleteSale(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Sale deleted successfully")
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateSale(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<SaleResponse>builder()
                .success(true)
                .message("Sale updated successfully")
                .data(saleService.partialUpdateSale(id, fieldsToUpdate))
                .build());
    }

    // Cập nhật Sale theo ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable UUID id, @RequestBody SaleDTO saleDTO) {

        return ResponseEntity.ok(DataResponse.<SaleResponse>builder()
                .success(true)
                .message("Sale updated successfully")
                .data(saleService.updateSale(id, saleDTO))
                .build());
    }
}
