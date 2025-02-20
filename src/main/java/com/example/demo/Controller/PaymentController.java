package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.PaymentDTO;
import com.example.demo.DTO.SaleDTO;
import com.example.demo.Service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 1. Get all payments
    @GetMapping
    public ResponseEntity<?> getAllPayments() {

        return ResponseEntity.ok(DataResponse.<List<PaymentDTO>>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(paymentService.getAllPayments())
                .build());
    }

    // 2. Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable UUID id) {


        return ResponseEntity.ok(DataResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(paymentService.getPaymentById(id))
                .build());

    }

    // Create a new payment
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO paymentDTO) {

        return ResponseEntity.ok(DataResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment created successfully")
                .data(paymentService.createPayment(paymentDTO))
                .build());
    }

    // 4. Update payment by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable UUID id, @RequestBody PaymentDTO paymentDTO) {

        return ResponseEntity.ok(DataResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment updated successfully")
                .data(paymentService.updatePayment(id, paymentDTO))
                .build());

    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdatePayment(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment updated successfully")
                .data(paymentService.partialUpdatePayment(id, fieldsToUpdate))
                .build());
    }


    // 5. Delete payment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable UUID id) {

            paymentService.deletePayment(id);
        return ResponseEntity.ok(DataResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment updated successfully")
                .build());
    }
}