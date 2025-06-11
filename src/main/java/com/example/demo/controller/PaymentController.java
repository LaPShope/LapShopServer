package com.example.demo.controller;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.PaymentDTO;
import com.example.demo.dto.request.payment.CreateTransaction;
import com.example.demo.dto.response.PaymentResponse;
import com.example.demo.service.PaymentIntegrationService;
import com.example.demo.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentIntegrationService paymentIntegrationService;

    public PaymentController(PaymentService paymentService, PaymentIntegrationService paymentIntegrationService) {
        this.paymentService = paymentService;
        this.paymentIntegrationService = paymentIntegrationService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllPaymentsByCustomerId() {

        return ResponseEntity.ok(DataResponse.<List<PaymentResponse>>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(paymentService.getAllPaymentsByCustomer())
                .build());
    }

    // 1. Get all payments
    @GetMapping("/admin")
    public ResponseEntity<?> getAllPayments() {

        return ResponseEntity.ok(DataResponse.<List<PaymentResponse>>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(paymentService.getAllPayments())
                .build());
    }

    // 2. Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(DataResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(paymentService.getPaymentById(id))
                .build());

    }

    // Create a new payment
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(DataResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment created successfully")
                .data(paymentService.createPayment(paymentDTO))
                .build());
    }

    @PostMapping("/{type}")
    public ResponseEntity<DataResponse<Map<String, Object>>> createPayment(
            @PathVariable String type,
            @RequestBody CreateTransaction createTransaction,
//            @RequestBody PaymentDTO paymentDTO,
            HttpServletRequest request
    ) throws Exception {
        type = type.toUpperCase();

        switch (type) {
            case "VNPAY":
                DataResponse<Map<String, Object>> dataResponse = DataResponse.<Map<String, Object>>builder()
                        .message("Add payment successfully").success(true)
                        .data(paymentIntegrationService.createOrder(request, createTransaction))
                        .build();
                return ResponseEntity.ok(dataResponse);
            case "MOMO":
                throw new Exception("Momo payment is not supported yet");

            case "ZALOPAY":
                throw new Exception("ZaloPay payment is not supported yet");

            default:
                throw new Exception("Don't have this type payment");
        }
    }

    // 4. Update payment by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable UUID id, @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(DataResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment updated successfully")
                .data(paymentService.updatePayment(id, paymentDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdatePayment(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<PaymentResponse>builder()
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

    @GetMapping("/ipn")
    public ResponseEntity<?> handlePayment(@RequestParam Map<String, String> params) {
        paymentService.handlePayment(params);
        return ResponseEntity.ok(DataResponse.builder()
                .message("Payment handled successfully")
                .success(true)
                .build());
    }
}