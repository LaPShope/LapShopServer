package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.PaymentMethodDTO;
import com.example.demo.DTO.SaleDTO;
import com.example.demo.Service.PaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    // Constructor Injection
    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPaymentMethods() {

        return ResponseEntity.ok(DataResponse.<List<PaymentMethodDTO>>builder()
                .success(true)
                .message("PaymentMethod retrieved successfully")
                .data(paymentMethodService.getAllPaymentMethods())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentMethodById(@PathVariable UUID id) {

        return ResponseEntity.ok(DataResponse.<PaymentMethodDTO>builder()
                .success(true)
                .message("PaymentMethod retrieved successfully")
                .data(paymentMethodService.getPaymentMethodById(id))
                .build());
    }


    @PostMapping
    public ResponseEntity<?> createPaymentMethod(@RequestBody PaymentMethodDTO paymentMethodDTO) {

        return ResponseEntity.ok(DataResponse.<PaymentMethodDTO>builder()
                .success(true)
                .message("PaymentMethod created successfully")
                .data(paymentMethodService.createPaymentMethod(paymentMethodDTO))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable UUID id, @RequestBody PaymentMethodDTO paymentMethodDTO) {

        return ResponseEntity.ok(DataResponse.<PaymentMethodDTO>builder()
                .success(true)
                .message("PaymentMethod updated successfully")
                .data(paymentMethodService.updatePaymentMethod(id, paymentMethodDTO))
                .build()); // HTTP 200 (OK)
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdatePaymentMethod(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<PaymentMethodDTO>builder()
                .success(true)
                .message("PaymentMethod updated successfully")
                .data(paymentMethodService.partialUpdatePaymentMethod(id, fieldsToUpdate))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable UUID id) {
            paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("PaymentMethod deleted successfully")
                .build());
    }
}